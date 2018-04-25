package kz.sdu;

public class Symbol {
    private String value;
    public Symbol(String string){
        this.value=string;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value){
        this.value=value;
    }
    public static boolean isTerminal(char input){
        return Utils.isNumeric(String.valueOf(input))||Character.isLowerCase(input);
    }
    public static boolean isEmpty(String input){
        return input.equals("$");
    }

    @Override
    public String toString() {
        return getValue();
    }
}
