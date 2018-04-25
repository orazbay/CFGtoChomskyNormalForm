package kz.sdu;

import java.util.*;

public class Variable  extends Symbol {

    public  LinkedList<ArrayList<Symbol>> endpoints=new LinkedList<>();


    public static LinkedList<String> variableNames=new LinkedList<>();

    public Variable(String string) {
        super(string);
        variableNames.add(string);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        variableNames.add(value);
    }

    public boolean isStart(){
        return getValue().equals("§");
    }

    @Override
    public String toString() {
        String result=getValue()+" -> ";
        for (ArrayList<Symbol> symbols:endpoints){
            String endpoint="";
            for (Symbol symbol:symbols){
                endpoint=endpoint.concat(symbol.getValue());
            }
            result=result.concat(endpoint.concat("|"));
        }
        return result.substring(0,result.length()-1);
    }
    public ArrayList<Integer>   hasUnitVariable(){
        ArrayList<Integer> unitVariableIndexes=new ArrayList<>();
        for (ArrayList<Symbol> symbols:endpoints){
            if (symbols.size()==1&&symbols.get(0) instanceof Variable){
                unitVariableIndexes.add(endpoints.indexOf(symbols));
            }
        }
        Collections.reverse(unitVariableIndexes);
        return unitVariableIndexes;
    }
    public  boolean hasEmpty(){
        for (ArrayList<Symbol> symbols:endpoints){
            if (symbols.get(0) instanceof Empty){
                return true;
            }
        }
        return false;
    }
    public void removeEmpty(){
        int emptyIndex=-1;
        for (ArrayList<Symbol> symbols:endpoints){
            if (symbols.get(0) instanceof Empty){
                emptyIndex=endpoints.indexOf(symbols);
                break;
            }
        }
        if (emptyIndex!=-1){
            endpoints.remove(emptyIndex);
        }
    }
    public boolean canAdd(String endpointInput){
        if (endpointInput.equals("")){
            return false;
        }
        return !toStringEndpoints().contains(endpointInput);
    }
    public ArrayList<String> toStringEndpoints(){
        ArrayList<String> stringEndpoints=new ArrayList<>();
        for(ArrayList<Symbol> endpoint:endpoints){
            String endpI="";
            for (Symbol symbol:endpoint){
                endpI=endpI.concat(symbol.getValue());
            }
            stringEndpoints.add(endpI);
        }
        return stringEndpoints;
    }
}
