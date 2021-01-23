var a = 0;
var b = 1;

while (a < 10) {
    print a;
    var temp = a;
    a = b;
    b = temp + b;
}