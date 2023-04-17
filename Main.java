import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    static List<String> OPERATION = Arrays.asList("!", "|", "-", "&", "(", ")");
    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String[] startString = reader.readLine().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\r", "").replaceAll("!!", "").replaceAll(">","").split("");
//        long time2 = System.nanoTime();

        List<String> parsString = new ArrayList<>();
        String currentVar = "";
        SortedSet<String> arrayVar = new TreeSet<>();
        for (String character : startString) {
            if (!OPERATION.contains(character)) {
                currentVar += character;
            } else {
                parsString.add(currentVar);
                arrayVar.add(currentVar);
                currentVar = "";
            }
            if (OPERATION.contains(character)) {
                parsString.add(character);
            }
        }

        parsString.add(currentVar);
        arrayVar.add(currentVar);

        parsString.removeAll(Collections.singleton(""));
        arrayVar.removeAll(Collections.singleton(""));

        Object[] obj = arrayVar.toArray();
        int countVar =  obj.length;

        for (int i = 0; i < countVar; i++){
            for (int j = 0; j < parsString.size(); j++){
                if (parsString.get(j).compareTo((String) obj[i]) == 0){
                    parsString.set(j, String.valueOf(i));
                }
            }
        }

        Stack<String> operations = new Stack<>();
        List<String> result = new ArrayList<>();

        for (String symb : parsString) {
            if (symb.equals("(")) {
                operations.add(symb);
            } else if (OPERATION.contains(symb)) {
                if (operations.isEmpty()) {
                    operations.add(symb);
                } else if (symb.equals(")")) {
                    while (true) {
                        String current = operations.get(operations.size() - 1);
                        operations.pop();
                        if (current.equals("(")) {
                            break;
                        }
                        result.add(current);
                    }
                } else if (priority(operations.get(operations.size() - 1)) <= priority(symb)) {
                    operations.push(symb);
                } else {
                    while (true) {
                        if (operations.isEmpty()) {
                            break;
                        }

                        String current = operations.peek();
                        if (priority(current) <= priority(symb)) {
                            break;
                        }
                        result.add(current);
                        operations.pop();
                    }
                    operations.push(symb);
                }
            } else {
                result.add(symb);
            }
        }

        while (!operations.isEmpty()) {
            String current = operations.peek();
            result.add(current);
            operations.pop();
        }


        List<List<Integer>> bTable = new ArrayList<>();
        for (int i = 0; i < countVar; i++) {
            bTable.add(new ArrayList<>());
            for (int l = 0; l < Math.pow(2, i); l++) {
                final double floor = Math.floor(Math.pow(2, countVar) / (Math.pow(2, i + 1)));
                for (int k = 0; k < floor; k++) {
                    bTable.get(i).add(0);
                }
                for (int k = 0; k < floor; k++) {
                    bTable.get(i).add(1);
                }
            }

        }

        Stack<List<Integer>> stack = new Stack<>();
        String[] p = result.toArray(new String[0]);

        for (String m : p) {
            if (isDigit(m)) {
                stack.push(bTable.get(Integer.parseInt(m)));
            } else if (m.equals("!")) {
                List<Integer> a1 = stack.pop();
                stack.add(negation(a1));
            } else {
                List<Integer> a2 = stack.pop();
                List<Integer> a1 = stack.pop();
                stack.push(simpleCalculate(a1, a2, m));
            }
        }

        Integer[] res = stack.pop().toArray(new Integer[0]);
//        System.out.println(Arrays.toString(res));

        int countNulls=0;
        for (Integer integer : res){
            if (integer == 0) countNulls++;
        }

        if (countNulls == (int) Math.pow(2, countVar)){
            System.out.println("Unsatisfiable");
        }
        else if ( countNulls == 0){
            System.out.println("Valid");
        }
        else{
            int an = (int) Math.pow(2, countVar) - countNulls;
            System.out.println("Satisfiable and invalid, " + an + " true and " + countNulls +" false cases");
        }
//        time2 = System.nanoTime() - time2;
//        System.out.printf("Elapsed %,9.6f ms\n", time2/1_000_000.0);

    }
    public static int priority(String operator) {
        if (operator.compareTo("(") == 0){
            return 0;}
        else if (operator.compareTo(">") == 0){
            return 1;}
        else if (operator.compareTo("|") == 0){
            return 2;}
        else if (operator.compareTo("&") == 0){
            return 3;}
        else if (operator.compareTo("!") == 0){
            return 4;
        }
        return 0;
    }

    public static List<Integer> negation(List<Integer> A) {
        List<Integer> result = new ArrayList<>();
        for (Integer integer : A) {
            if (integer == 1) {
                result.add(0);
            } else {
                result.add(1);
            }
        }
        return result;
    }

    public static List<Integer> simpleCalculate(List<Integer> A, List<Integer> B, String operator) {
        List<Integer> result = new ArrayList<>();
        switch (operator) {
            case "-":
                for (int i = 0; i < A.size(); i++) {
                    if (A.get(i) <= B.get(i)) {
                        result.add(1);
                    } else {
                        result.add(0);
                    }
                }
                break;
            case "&":
                for (int i = 0; i < A.size(); i++) {
                    if (A.get(i).equals(B.get(i)) && A.get(i) != 0) {
                        result.add(1);
                    } else {
                        result.add(0);
                    }
                }
                break;
            case "|":
                for (int i = 0; i < A.size(); i++) {
                    if (A.get(i) == 1 || B.get(i) == 1) {
                        result.add(1);
                    } else {
                        result.add(0);
                    }
                }
                break;
        }

        return result;
    }

    public static boolean isDigit(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
