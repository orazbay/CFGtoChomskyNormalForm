package kz.sdu;

import java.lang.reflect.Array;
import java.util.*;

public class Transformer {
    ArrayList<Variable> variables;
    public Transformer(ArrayList<Variable> variables){
        this.variables=variables;
    }



    private Variable getVariableFromList(String symbol){
        for (Variable variable:variables){
            if (variable.getValue().equals(symbol)){
                return variable;
            }
        }
        return null;
    }
    public void transform() {
        addStartVariable();
        System.out.println("Step 1 completed: \n"+variables.toString());
        removeEmpties();
        System.out.println("Step 2 completed: \n"+variables.toString());
        removeUnitRules();
        System.out.println("Step 3 completed: \n"+variables.toString());
        removeMoreThanTwoSymbols();
        System.out.println("Step 4 completed: \n"+variables.toString());
        eliminateTerminals();
        System.out.println("Step 5 completed: \n"+variables.toString());


    }


    //step 1
    public void addStartVariable(){
            Variable variable=new Variable("§");
            ArrayList<Symbol> single=new ArrayList<>();
            single.add(variables.get(0));
            variable.endpoints.add(single);
            variables.add(0,variable);

    }






    //step2
    public  void removeEmpties(){
        for (Variable variable:variables){
            ArrayList<ArrayList<Symbol>>endpointsNew=new ArrayList<>();
            for (ArrayList<Symbol> symbols:variable.endpoints) {
                ArrayList<Symbol> newEndpoints = new ArrayList<>();
                boolean addNewEndpoint = false;
                ArrayList<String> variablesToPermutate = new ArrayList<>();
                for (Symbol symbol : symbols) {

                    if (symbol instanceof Variable && getVariableFromList(symbol.getValue()).hasEmpty()) {
                        addNewEndpoint = true;
                        variablesToPermutate.add(symbol.getValue());
                        newEndpoints.add(new Symbol("substitute"));
                    } else {
                        newEndpoints.add(symbol);
                    }
                }
                Set<String> setOfNewEndpoints=new HashSet<>();
                if (addNewEndpoint&&!(newEndpoints.get(0).getValue().equals("substitute")&&newEndpoints.size()==1)) {
                    for (ArrayList<String> combination : getAllPossibilites(variablesToPermutate)) {
                        Iterator<String> iterator = combination.iterator();
                        ArrayList<Symbol> endPoint = new ArrayList<>();
                        String endPointString="";
                        for (Symbol endP : newEndpoints) {
                            String enpointStringI="";
                            if (endP.getValue().equals("substitute")) {
                                String sym = iterator.next();
                                if (!sym.equals("")) {
                                    endPoint.add(getVariableFromList(sym));
                                    enpointStringI=sym;
                                }
                            } else {
                                endPoint.add(endP);
                                enpointStringI=endP.getValue();
                            }
                            endPointString=endPointString+enpointStringI;
                        }
                        if (variable.canAdd(endPointString)&&setOfNewEndpoints.add(endPointString)) {
                            endpointsNew.add(endPoint);
                        }
                    }
                }
            }
            if (endpointsNew.size()>0) {
                variable.endpoints.addAll(endpointsNew);
            }
            variable.removeEmpty();

        }
        if (!variables.get(0).hasEmpty()){
            ArrayList<Symbol> single=new ArrayList<>();
            single.add(new Empty());
            variables.get(0).endpoints.add(single);
        }
    }

    //step3
    public void removeUnitRules(){
        for (Variable variable:variables) {
            for (int unitRuleIndex:variable.hasUnitVariable()){
                Variable variable1=getVariableFromList(variable.endpoints.get(unitRuleIndex).get(0).getValue());
                LinkedList<ArrayList<Symbol>> endPoints=variable1.endpoints;
                variable.endpoints.remove(unitRuleIndex);
                if (variable1!=variable){
                    variable.endpoints.addAll(endPoints);
                }
            }
            variable.endpoints.removeIf(a-> a.size()==1&&a.get(0) instanceof Variable);
        }
    }

    //Step 4: Eliminate all rules having more than two symbols on the right-hand side
    public void removeMoreThanTwoSymbols(){
        ArrayList<Variable> newVariables=new ArrayList<>();
        for(Variable variable:variables){
            ArrayList<ArrayList<Symbol>> arrayToSubstitute=new ArrayList<>();
            ArrayList<ArrayList<Symbol>> arrayToDelete=new ArrayList<>();

            for (ArrayList<Symbol> symbols:variable.endpoints){
                ArrayList<Symbol> ToSubstitute=new ArrayList<>();
                if (symbols.size()>2){

                    arrayToDelete.add(symbols);

                    System.out.println("next Endpoint+"+symbols.toString());
                    int startSublist=0;
                    int endSublist=2;
                    while (true){
                        ArrayList<Symbol> endPointOfNewVariable= new ArrayList<>(symbols.subList(startSublist,endSublist));
                        System.out.println("new endpoint+"+endPointOfNewVariable.toString());
                        Variable newVariable=new Variable("new");
                        newVariable.endpoints.add(endPointOfNewVariable);
                        ToSubstitute.add(newVariable);

                        newVariables.add(newVariable);

                        startSublist=endSublist;
                        endSublist=endSublist+2;
                        if (symbols.size()-endSublist<=2){
                            break;
                        }
                    }
                    ToSubstitute.addAll(symbols.subList(endSublist-2,symbols.size()));
                    arrayToSubstitute.add(ToSubstitute);
                }

            }
            if (arrayToSubstitute.size()>0){
                variable.endpoints.addAll(arrayToSubstitute);
                variable.endpoints.removeAll(arrayToDelete);
            }



        }
        for (Variable newVariable:newVariables){
            newVariable.setValue(getNextAvailableVariable());
            variables.add(newVariable);
        }
    }

    //Step 5: Eliminate all rules, whose right-hand side contains exactly two symbols, which are not both variables
    public void eliminateTerminals(){
        Map<String,Variable> terminalNewVariableMap=new HashMap<>();
        for (Variable variable:this.variables){

            for(ArrayList<Symbol> endpoint:variable.endpoints){
                ArrayList<Symbol> terminalsToSwap=new ArrayList<>();
                for (Symbol symbol:endpoint){
                    if (!symbol.getValue().equals("")&&Symbol.isTerminal(symbol.getValue().charAt(0))){
                        terminalsToSwap.add(symbol);
                        if (!terminalNewVariableMap.containsKey(symbol.getValue())) {
                            Variable newVariable=new Variable("");
                            newVariable.endpoints.add(new ArrayList<Symbol>(Arrays.asList(symbol)));
                            terminalNewVariableMap.put(symbol.getValue(),newVariable);
                        }
                    }
                }

                for (Symbol terminalToSwap :terminalsToSwap){
                    endpoint.set(endpoint.indexOf(terminalToSwap),terminalNewVariableMap.get(terminalToSwap.getValue()));
                }
            }


        }
        for (String terminalValue:terminalNewVariableMap.keySet()){
            terminalNewVariableMap.get(terminalValue).setValue(getNextAvailableVariable());
            variables.add(terminalNewVariableMap.get(terminalValue));
        }


    }

    private ArrayList<ArrayList<String>> getAllPossibilites(ArrayList<String> input){
        ArrayList<ArrayList<String>> result=new ArrayList<>();
        for (String string:combinations(input.size())) {
                ArrayList<String> comb = new ArrayList<>(input);
                for (String string1 : string.split("")) {
                    comb.set(Integer.valueOf(string1), "");

                }
                result.add(comb);

        }
        return result;

    }
    private static String[] combinations(int n) {
        String [] array=new String [n];
        for (int u=0;u<n;u++){
            array[u]=String.valueOf(u);
        }
        String[] res = new String[(1 << array.length) - 1];
        int k = 0;
        int x = 1;
        for (int i = array.length - 1; i >= 0; --i) {
            res[k++] = array[i];
            for (int j = 1; j < x; ++j) {
                res[k++] = array[i] + res[j - 1];
            }
            x *= 2;
        }
        return res;
    }
//    private String getNextAvailableVariable(){
//        int last=0;
//        for (Variable variable:variables){
//            if (!variable.isStart()) {
//                int code = Character.codePointAt(variable.getValue(), 0);
//                if (code > last) {
//                    last = code;
//                }
//            }
//        }
//        return String.valueOf(new Character((char)(last+1)));
//    }
    private String getNextAvailableVariable(){
        for (int i=65;i<93;i++){
            String variableName=String.valueOf(new Character((char) i));
            if (!Variable.variableNames.contains(variableName)){
                return variableName;
            }
        }
        return null;
    }



}
