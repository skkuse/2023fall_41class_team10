var _chart = document.querySelector('.chart');
var _chartBar = document.querySelectorAll('.chart-bar');
var color = ['#9986dd','#fbb871','#bd72ac','#f599dc'] //색상
var newDeg = []; //차트 deg

function insertAfter(newNode, referenceNode) {
    referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}

function chartLabel(){
    var _div = document.createElement('div');
    _div.className = 'chart-total';
    _div.innerHTML = `
  <span class="chart-total-text1">Core</span>
  <span class="chart-total-text2">Memory</span>`;
    insertAfter(_div,_chart);
}

function chartDraw(){
    var num1 = _chartBar[0].dataset.deg;
    var num2 = _chartBar[1].dataset.deg;

    _chart.style.background = 'conic-gradient(#9986dd ' +
        num1 + 'deg, #9986dd ' + num1 + 'deg, #fbb871 ' +
        num1 + 'deg ' + num2 + 'deg, #bd72ac ' +
        (parseInt(num1) + parseInt(num2)) + 'deg )';

    chartLabel();
}

function chartDraw2(){
    var num3 = _chartBar[0].dataset.deg;
    var num4 = _chartBar[1].dataset.deg;

    _chart.style.background = 'conic-gradient(#ccc ' +
        num3 + 'deg, #ccc ' + num3 + 'deg, #ccc ' +
        num3 + 'deg ' + num4 + 'deg, #ccc ' +
        (parseInt(num3) + parseInt(num4)) + 'deg )';

    chartLabel();
}

function btnClick(){
    var btnSubmit=document.getElementById("btn-code");
    btnSubmit.style.backgroundColor = "#183E0C";
    btnSubmit.textContent="running";
    btnSubmit.style.pointerEvents="none";

    // var textarea=document.getElementById("code-input");
    // textarea.style.border="2px solid #183E0C";
    // textarea.style.backgroundColor="white";
    // textarea.style.padding="3px";
    var codeMirrorWrapper = document.querySelector(".CodeMirror");
    codeMirrorWrapper.style.border="3px solid #183E0C";
    codeMirrorWrapper.style.padding="2px";
    codeMirrorWrapper.style.backgroundColor="#85917c";
    var codeMirrorWrapper_gutter = document.querySelector(".CodeMirror-gutter");
    codeMirrorWrapper_gutter.style.backgroundColor="#85917c";

    var chart1=document.getElementById("chart1");
    chart1.setAttribute("data-deg", "100");

    chartDraw();
}


chartDraw2();
