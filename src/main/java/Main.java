import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Informe o nome do trabalhador:");
        String nome = scanner.nextLine();

        System.out.println("Informe o salário bruto:");
        double salarioBruto = scanner.nextDouble();

        System.out.println("Informe o desconto do INSS:");
        double descontoINSS = scanner.nextDouble();

        System.out.println("Informe o número de dependentes:");
        int numDependentes = scanner.nextInt();

        System.out.println("Informe o valor total de descontos para dedução de IRRF:");
        double descontosIRRF = scanner.nextDouble();

        System.out.println("Informe o CPF:");
        String cpf = scanner.next();

        if (!validarCPF(cpf)) {
            System.out.println("CPF inválido!");
            return;
        }

        System.out.println("Informe o CEP:");
        String cep = scanner.next();

        String endereco = consultarCEP(cep);

        double salarioLiquido = calcularSalarioLiquido(salarioBruto, descontoINSS, numDependentes, descontosIRRF);

        try {
            FileWriter fileWriter = new FileWriter("trabalhadores.txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println(nome + "," + salarioBruto + "," + descontoINSS + "," + numDependentes + "," +
                                descontosIRRF + "," + cpf + "," + cep + "," + endereco + "," + salarioLiquido);

            printWriter.close();
        } catch (IOException e) {
            System.out.println("Erro ao escrever no arquivo.");
        }

        System.out.println("Salário líquido calculado e informações armazenadas com sucesso!");
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
        // Lógica de validação do CPF aqui
        return true; // Simplificado para o exemplo
    }

    private static String consultarCEP(String cep) {
        // Lógica de consulta do CEP aqui
        return "Rua Exemplo, 123 - Bairro Exemplo, Cidade Exemplo";
    }
  
}
