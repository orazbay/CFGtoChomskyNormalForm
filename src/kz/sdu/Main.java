package kz.sdu;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.util.*;

public class Main {


    public static void main(String[] args) {
        Map<String,ArrayList<String>> input = new HashMap<>();
        ArrayList<Variable> variables=new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line=scanner.nextLine().trim();
            if (line.equals("end")){
                break;
            }
            String [] splitedLine1=line.split("->");


            String [] endpoints=splitedLine1[1].split("\\|");


            String keyVariable=splitedLine1[0];
            input.put(keyVariable,new ArrayList<>(Arrays.asList(endpoints)));

            Variable variable=new Variable(keyVariable);


            for (String endpoint:endpoints){
                char [] symbols=endpoint.toCharArray();
                ArrayList<Symbol> endpointsOfVariable=new ArrayList<>();

                for (char symbol:symbols){
                    Symbol symbolLocal;
                    if (Symbol.isTerminal(symbol)){
                        symbolLocal=new Terminal(String.valueOf(symbol));
                    }else if (Symbol.isEmpty(String.valueOf(symbol))){
                        symbolLocal=new Empty();
                    }
                    else  {
                        symbolLocal=new Variable(String.valueOf(symbol));
                    }
                    endpointsOfVariable.add(symbolLocal);
                }

                variable.endpoints.add(endpointsOfVariable);



            }
            variables.add(variable);
        }
        System.out.println(variables.toString());
        Transformer transformer=new Transformer(variables);
        transformer.transform();

    }

}
