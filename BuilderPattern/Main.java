import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Client client = new Client();

        System.out.print("Enter Contract Type (Permanent, LongTerm, ShortTerm): ");
        String contractType = sc.nextLine().trim();

        Contract builder;
        switch (contractType.toLowerCase()) {
            case "permanent":
                builder = new Permanent();
                break;
            case "longterm":
                builder = new LongTerm();
                break;
            case "shortterm":
                builder = new ShortTerm();
                break;
            default:
                System.out.println("Invalid contract type.");
                return;
        }

        System.out.print("Enter Contract ID: ");
        String contractID = sc.nextLine().trim();

        System.out.print("Enter Property ID: ");
        String propertyID = sc.nextLine().trim();

        System.out.print("Enter Tenant ID: ");
        String tenantID = sc.nextLine().trim();

        System.out.print("Enter Rent Amount: ");
        double rentAmount = sc.nextDouble();
        sc.nextLine(); 

        RentalContract contract = client.requestCreateRentalContract(builder, contractID, propertyID, tenantID,
                rentAmount, contractType);

        System.out.println("\nRental contract created successfully!");
        System.out.println(contract.toString());

    }

    public static String saveContractToFile(RentalContract contract) {
        try {
            File directory = new File("contracts");
            if (!directory.exists()) {
                directory.mkdir();
            }

            String fileName = "contracts/" + contract.getContractID() + "_Contract.txt";
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(contract.toString() + "\n");
            writer.close();

            System.out.println("Contract saved to file: " + fileName);
            return fileName;
        } catch (IOException e) {
            System.out.println("Error saving contract to file: " + e.getMessage());
            return null;
        }
    }

    public static void encryptAndSaveContract(String filePath) {
        if (filePath == null) {
            System.out.println("Error: No contract file found to encrypt.");
            return;
        }

        try {
            SecretKey key = AES256Example.generateKey();

            String contractData = AES256Example.readFile(filePath);
            if (contractData == null) {
                System.out.println("No contract data found!");
                return;
            }

            String encryptedData = AES256Example.encrypt(contractData, key);
            System.out.println("Encrypted Contract: " + encryptedData);

            String encryptedFilePath = filePath + "_encrypted.txt";
            AES256Example.writeFile(encryptedFilePath, encryptedData);
            System.out.println("Encrypted contract saved to: " + encryptedFilePath);

            System.out.print("Do you want to decrypt the file? (yes/no): ");
            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("yes")) {
                String readEncryptedData = AES256Example.readFile(encryptedFilePath);
                if (readEncryptedData != null) {
                    String decryptedData = AES256Example.decrypt(readEncryptedData, key);
                    System.out.println("Decrypted Contract: \n" + decryptedData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
