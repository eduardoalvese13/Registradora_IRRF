import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Informe o nome do trabalhador:");
            String nome = scanner.nextLine();

            double salarioBruto = lerDouble(scanner, "Informe o salário bruto:");
            double descontoINSS = lerDouble(scanner, "Informe o desconto do INSS:");
            int numDependentes = lerInt(scanner, "Informe o número de dependentes:");
            double descontosIRRF = lerDouble(scanner, "Informe o valor total de descontos para dedução de IRRF:");

            System.out.println("Informe o CPF:");
            String cpf = scanner.next();

            if (!validarCPF(cpf)) {
                System.out.println("CPF inválido!");
                return;
            }

            System.out.println("Informe o CEP:");
            String cep = scanner.next();

            String endereco = consultarCEP(cep);
            if (endereco == null) {
                System.out.println("CEP inválido!");
                return;
            }

            double salarioLiquido = calcularSalarioLiquido(salarioBruto, descontoINSS, numDependentes, descontosIRRF);

            try (FileWriter fileWriter = new FileWriter("trabalhadores.txt", true);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {

                printWriter.println(nome + "," + salarioBruto + "," + descontoINSS + "," + numDependentes + "," +
                        descontosIRRF + "," + cpf + "," + cep + "," + endereco + "," + salarioLiquido);
            } catch (IOException e) {
                System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
            }

            System.out.println("Salário líquido calculado e informações armazenadas com sucesso!");

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static double lerDouble(Scanner scanner, String mensagem) {
        while (true) {
            try {
                System.out.println(mensagem);
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, insira um número válido.");
                scanner.next(); // Limpa a entrada inválida
            }
        }
    }

    private static int lerInt(Scanner scanner, String mensagem) {
        while (true) {
            try {
                System.out.println(mensagem);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, insira um número válido.");
                scanner.next(); // Limpa a entrada inválida
            }
        }
    }

    private static double calcularSalarioLiquido(double salarioBruto, double descontoINSS, int numDependentes, double descontosIRRF) {
        double salarioBase = salarioBruto - descontoINSS - (numDependentes * 189.59) - descontosIRRF;
        double impostoIRRF;

        if (salarioBase <= 1903.98)
            impostoIRRF = 0;
        else if (salarioBase <= 2826.65)
            impostoIRRF = (salarioBase * 0.075) - 142.80;
        else if (salarioBase <= 3751.05)
            impostoIRRF = (salarioBase * 0.15) - 354.80;
        else if (salarioBase <= 4664.68)
            impostoIRRF = (salarioBase * 0.225) - 636.13;
        else
            impostoIRRF = (salarioBase * 0.275) - 869.36;

        return salarioBase - impostoIRRF;
    }

    private static boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int[] pesos = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * pesos[i];
        }
        int primeiroDigito = 11 - (soma % 11);
        primeiroDigito = (primeiroDigito >= 10) ? 0 : primeiroDigito;

        pesos = new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * pesos[i];
        }
        int segundoDigito = 11 - (soma % 11);
        segundoDigito = (segundoDigito >= 10) ? 0 : segundoDigito;

        return cpf.charAt(9) == Character.forDigit(primeiroDigito, 10) &&
                cpf.charAt(10) == Character.forDigit(segundoDigito, 10);
    }

    private static String consultarCEP(String cep) {
        String endereco = null;
        try {
            URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            JSONTokener tokener = new JSONTokener(isr);
            JSONObject jsonObject = new JSONObject(tokener);

            if (!jsonObject.has("erro")) {
                String logradouro = jsonObject.getString("logradouro");
                String bairro = jsonObject.getString("bairro");
                String localidade = jsonObject.getString("localidade");
                String uf = jsonObject.getString("uf");
                endereco = logradouro + ", " + bairro + " - " + localidade + ", " + uf;
            }
        } catch (Exception e) {
            System.out.println("Erro ao consultar o CEP: " + e.getMessage());
        }

        return endereco;
    }
}
