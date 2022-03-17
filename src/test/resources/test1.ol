program CharsTest

class CharClass {
    char ch;
    {
    void setChar(char c) {
        this.ch = c;
    }
    void printChar() {
        print(this.ch)
    }
    }
}

{
    void main()
    CharClass charA;
    CharClass charB;
    {
       charA = new CharClass;
       charB = new CharClass;

       charA.setChar('a');
       charB.setChar('b');

       charA.printChar();
       charB.printChar();
    }
}