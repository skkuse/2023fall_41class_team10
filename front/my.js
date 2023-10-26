function updateCountryList() {
  var regionSelect = document.getElementById("region");
  var countrySelect = document.getElementById("country");
  countrySelect.innerHTML = ""; // 이전 목록 지우기

  var region = regionSelect.value;

  // 선택한 지역에 따라 나라 목록 추가
  if (region === "아시아") {
    var countries = ["Select Country", "한국", "중국", "일본", "인도"];
  } else if (region === "유럽") {
    var countries = ["Select Country", "독일", "프랑스", "영국", "이탈리아"];
  } else if (region === "아프리카") {
    var countries = [
      "Select Country",
      "나이지리아",
      "남아프리카",
      "케냐",
      "이집트",
    ];
  }

  // 나라 목록에 옵션 요소 추가
  for (var i = 0; i < countries.length; i++) {
    var option = document.createElement("option");
    option.text = countries[i];
    option.value = countries[i];
    countrySelect.appendChild(option);
  }
}

var carbonintensity;
var selectOk = 0;
function selectCountry() {
  var countrySelect = document.getElementById("country");
  var selectedCountry = countrySelect.value; // 선택한 나라 값 가져오기

  if (selectedCountry === "한국") {
    selectOk = 1;
    carbonintensity = "hello";
  } else if (selectedCountry === "중국") {
    selectOk = 1;
    carbonintensity = "hi";
  } else if (selectedCountry === "프랑스") {
    selectOk = 1;
    carbonintensity = "bon";
  } else if (selectedCountry === "Select Country") {
    carbonintensity = "You didn't select Country";
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const btnCode = document.getElementById("btn-code");

  btnCode.addEventListener("click", function () {
    const codeInput = document.getElementById("code-input");
    const textContent = codeInput.value;

    if (selectOk === 1) {
      //실제로는 코드를 돌려서 화면에 parameter 표시하는 작업 필요한 부분
      alert(textContent + carbonintensity);
    } else {
      alert("You didn't select Country");
    }
  });
});

// 초기 목록 설정
updateCountryList();
