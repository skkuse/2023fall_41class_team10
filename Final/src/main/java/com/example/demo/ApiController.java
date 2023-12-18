package com.example.demo;
import javax.tools.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ApiController {
    @PostMapping("/api")
    public String handlePostRequest(@RequestBody String payload) throws Exception {
        // Json String에서 key 값 얻기
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(payload, Map.class);
        String key = (String) map.get("key");

        // 사용자로부터 받은 소스 코드
        String userCode = UserCodeWithGetInfo(key);

        if (userCode == null){
            System.out.println("Compilation failed.");
            return "{\"runtime_flag\":\"" + "false" + "\"}";
        }

        // 소스 코드를 파일로 저장
        File sourceFile = new File("UserCode.java");
        try (PrintWriter writer = new PrintWriter(sourceFile)) {
            writer.println(userCode);
        }

        // Compiler 가져오기
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 컴파일 진행 중 오류를 담을 DiagnosticCollector 생성
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        // 컴파일
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        boolean success = task.call();

        // 컴파일 성공 여부에 따른 처리
        if (!success) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
            }
            System.out.println("Compilation failed.");
            return "{\"runtime_flag\":\"" + "false" + "\"}";
        }


        // 실행
        Process process = Runtime.getRuntime().exec("java UserCode");

        // 타임아웃 설정
        final int TIMEOUT = 10000;
        try {
            // 프로세스의 종료를 기다립니다. 지정된 시간 내에 종료되지 않으면 TimeoutException을 던집니다.
            if (!process.waitFor(TIMEOUT, TimeUnit.MILLISECONDS)) {
                // 타임아웃이 지나도 프로세스가 아직 실행 중이면 강제로 종료합니다.
                process.destroy();
                System.out.println("Execution timed out.");
                //return "";
                return "{\"runtime_timeout\":\"" + "true" + "\"}";
            }
        } catch (InterruptedException e) {
            // InterruptedException이 발생하면 루프를 계속합니다.
        }

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String javaVersion = System.getProperty("java.version");

        String dataSet = "OS Name: " + osName + "/OS Version: " + osVersion + "/Java Version: " + javaVersion;

        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataSet += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Runtime 값 추출
        String runtimeSubstring = extractValue(dataSet, "Runtime: ");
        double runtime = Double.parseDouble(runtimeSubstring);


        // Used Memory 값 추출 (0.3725W/GB)
        String memorySubstring = extractValue(dataSet, "Used Memory: ");
        int memory = Integer.parseInt(memorySubstring);

        //PUE, PSF 값 설정
        double PUE = 1.67;
        int PSF = 1;

        //Core i7-10700K 가정, core 개수 8개, core power 15.6 (W) (usage = 1)
        int n_CPUcores = 8;
        double CPUpower = 15.6;
        int usageCPU_used = 1;

        //memoryPower (W/GB)
        double memoryPower = 0.3725;

        //carbonIntensity (Korea)
        double carbonIntensity = 415.6;

        //powerNeeded 계산 (W)
        double powerNeeded_core = PUE * n_CPUcores * CPUpower * usageCPU_used; //GPU는 사용 X
        double powerNeeded_memory = PUE * (memory/Math.pow(1024, 3) * memoryPower);
        double powerNeeded = powerNeeded_core + powerNeeded_memory;

        //energyNeeded 계산 (W -> kWh)
        double energyNeeded_core = runtime * powerNeeded_core * PSF / 1000;
        double energyNeeded_memory = runtime * powerNeeded_memory * PSF / 1000;
        double energyNeeded = runtime * powerNeeded * PSF / 1000;

        //carbonEmissions 계산 (gCO2)
        double CE_core = energyNeeded_core * carbonIntensity;
        double CE_memory  = energyNeeded_memory * carbonIntensity;
        double carbonEmissions = energyNeeded * carbonIntensity;

        //rounding
        energyNeeded = Math.round(energyNeeded*10000)/10000.0;
        carbonEmissions = Math.round(carbonEmissions*10000)/10000.0;
        CE_core = Math.round(CE_core*10000)/10000.0;
        CE_memory = Math.round(CE_memory*10000)/10000.0;

        //percentage
        double CE_core_per = CE_core / (CE_core + CE_memory) * 100;
        double CE_memory_per = CE_memory / (CE_core + CE_memory) * 100;

        //treeMonths (days)
        double treeDays = carbonEmissions * 30/ 11000 * 12;
        treeDays = Math.round(treeDays*10000)/10000.0;

        //train (km)
        double train = carbonEmissions / 41;
        train = Math.round(train*10000)/10000.0;

        //lightbulb 60W (hour)
        double lightbulb = energyNeeded * 1000 / 60;
        lightbulb = Math.round(lightbulb*10000)/10000.0;

        //car (km)
        double car = carbonEmissions / 251;
        car = Math.round(car*10000)/10000.0;

        System.out.println("Compilation success.");
        return "{\"runtime\":\"" + runtime + " (sec)" + "\", \"coreType\":\"" + "CPU" + "\", \"cpuModel\":\"" + "Core i7-10700K" + "\", \"coreNumber\":\"" + 8 + "\", \"cpuPower\":\"" + 15.6 + "\", \"cpuUsage\":\"" + 1 + "\", \"memory\":\"" + memory + "\", \"memoryPower\":\"" + memoryPower + "\", \"PUE\":\"" + PUE + "\", \"PSF\":\"" + PSF + "\", \"country\":\"" + "Korea" + "\", \"osName\":\"" + osName + "\", \"osVersion\":\"" + osVersion + "\", \"javaVersion\":\"" + javaVersion + "\", \"carbonEmissions\":\"" + carbonEmissions + "\", \"energyNeeded\":\"" + energyNeeded +"\", \"tree\":\"" + treeDays + "\", \"car\":\"" + car + "\", \"train\":\"" + train + "\", \"bulb\":\"" + lightbulb + "\", \"CE_core\":\"" + CE_core + "\", \"CE_memory\":\"" + CE_memory + "\", \"CE_core_per\":\"" + CE_core_per + "\", \"CE_memory_per\":\"" + CE_memory_per + "\"}";
    }

    public static String UserCodeWithGetInfo(String userCode)
    {
        if (!isValidJavaCode(userCode)) {
            return null; // Invalid Java code
        }

        String oldClassName = userCode.substring(userCode.indexOf("class") + 6, userCode.indexOf("{")).trim(); // 기존 클래스명 추출
        userCode = userCode.replace(oldClassName, "UserCode"); // 클래스명 교체

        int index = userCode.indexOf("public static void main");

        if (index == -1) {
            return null; // main 함수가 없습니다.
        }

        int braceOpenIndex = userCode.indexOf("{", index);
        if (braceOpenIndex == -1) {
            return null; // main 함수의 시작 중괄호 `{`가 없습니다.
        }

        int braceCount = 1;
        int braceCloseIndex = braceOpenIndex + 1;
        while (braceCount > 0 && braceCloseIndex < userCode.length()) {
            if (userCode.charAt(braceCloseIndex) == '{') {
                braceCount++;
            } else if (userCode.charAt(braceCloseIndex) == '}') {
                braceCount--;
            }
            braceCloseIndex++;
        }

        if (braceCount != 0) {
            return null; // main 함수의 시작 중괄호 `{`와 끝 중괄호 `}`가 일치하지 않습니다.
        }

        // 리소스 사용량 측정하는 코드 추가
        String importLib = "import java.io.*; ";
        String frontOfTheMain = "long startTime = System.currentTimeMillis();";
        String BackOfTheMain = "long endTime = System.currentTimeMillis(); Runtime.getRuntime().gc(); long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); String form = String.format(\"/Runtime: %f/Used Memory: %d\", (endTime - startTime) / 1000.0, usedMem); try (FileWriter writer = new FileWriter(\"output.txt\")) { 	writer.write(form); } catch (IOException e) { 	e.printStackTrace(); }";

        // main 함수의 시작 중괄호 다음과 끝 중괄호 앞에 문자열을 삽입합니다.
        StringBuilder sb = new StringBuilder(userCode);
        sb.insert(braceOpenIndex + 1, frontOfTheMain);
        sb.insert(braceCloseIndex + frontOfTheMain.length() - 1, BackOfTheMain);

        return importLib + sb.toString();
    }

    private static String extractValue(String input, String key) {
        int startIndex = input.indexOf(key) + key.length();
        int endIndex = input.indexOf("/", startIndex);
        if (endIndex == -1) {
            endIndex = input.length();
        }
        return input.substring(startIndex, endIndex).trim();
    }

    private static boolean isValidJavaCode(String userCode) {
        // You can add more sophisticated code validation here
        return userCode.contains("class") && userCode.contains("{") && userCode.contains("public static void main");
    }

}
