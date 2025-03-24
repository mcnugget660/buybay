package code.DataStorage;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import code.Util.ProductData;
import com.google.gson.Gson;

import code.DemoApplication;
import org.springframework.web.multipart.MultipartFile;

public class ProductManager {

    public static ProductManager PRODUCTMANAGER;

    private HashMap<Integer, Product> products = new HashMap<>();

    private HashMap<String, ArrayList<Product>> userProducts = new HashMap<>();

    private HashMap<String, ArrayList<Integer>> userCart = new HashMap<>();

    private HashMap<Integer, Integer> imageCount = new HashMap<>();

    private HashMap<Integer, Long> productOffset = new HashMap<>(); // In bytes

    private File file;

    private ArrayList<Product> productsSorted = new ArrayList<>();

    public ProductManager() throws FileNotFoundException, IOException {

        Gson g = new Gson();

        // Products
        if (!(file = new File(DemoApplication.PATH + "Storage/Products/productList")).createNewFile()) {
            // If the file exists scan it for a list of existing products and their
            // information

            RandomAccessFile raf = new RandomAccessFile(file, "r");

            while (true) {
                try {
                    long linePointer = raf.getFilePointer();
                    String line = raf.readLine();

                    if (line == null)
                        break;

                    if (line.equals(""))
                        continue;

                    try {
                        int images = Integer.valueOf(line.substring(0, line.indexOf(" ")));
                        String p = line.substring(line.indexOf(" ") + 1, line.length());

                        // Load product information
                        Product product = g.fromJson(p, Product.class);

                        products.put(product.getID(), product);
                        imageCount.put(product.getID(), images);
                        addProduct(product);

                        new File(DemoApplication.PATH + "Storage/Products/" + product.getID()).mkdirs();
                    } catch (Exception e) {
                        System.out.println("Failed to load product : " + line + ";\n");
                        e.printStackTrace();
                    }

                } catch (EOFException e) {
                    break;
                }
            }

            Collections.sort(productsSorted, new Comparator() {

                public int compare(Object a, Object b) {

                    return ((Product) a).getName().compareTo(((Product) b).getName());
                }
            });

            Product.setNextId(productsSorted.size());

        }

        //Cart

        new File(DemoApplication.PATH + "Storage/Products/userCart").createNewFile();

        Scanner scan = new Scanner(file);

        while (scan.hasNext()) {
            String line = scan.nextLine();

            if (line.isBlank() || line.isEmpty())
                continue;

            String[] components = line.split(" ");

            if (components.length == 2) {
                userCart.put(components[0], (ArrayList<Integer>) g.fromJson(components[1], ArrayList.class));
            }
        }
    }

    public static void initalize() throws IOException {
        PRODUCTMANAGER = new ProductManager();
    }

    public void onClose() {
        //on program end save carts override everything
        File f = new File(DemoApplication.PATH + "Storage/Products/userCart");
        f.delete();
        try {
            f.createNewFile();
            FileWriter fw = new FileWriter(f);

            Gson g = new Gson();

            for (Map.Entry<String, ArrayList<Integer>> cart : userCart.entrySet()) {
                fw.write(cart.getKey() + " " + g.toJson(cart.getValue()));
                fw.write(System.getProperty("line.separator")); // new line
            }

            fw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addProduct(Product p) {
        products.put(p.getID(), p);

        if (userProducts.containsKey(p.getSeller()))
            userProducts.get(p.getSeller()).add(p);
        else
            userProducts.put(p.getSeller(), new ArrayList<>(Arrays.asList(p)));


        // Insert with binary search later
        for (int i = 0; i < productsSorted.size(); i++) {
            if (productsSorted.get(i).getName().compareTo(p.getName()) < 0) {
                productsSorted.add(i + 1, p);
                return;
            }
        }
        productsSorted.add(p);
        System.out.println(productsSorted);
    }

    public ArrayList<Product> getUserProducts(String seller) {
        return userProducts.get(seller);
    }

    public void addToCart(String user, int ID) {
        if (userCart.containsKey(user))
            userCart.get(user).add(ID);
        else
            userCart.put(user, new ArrayList<>(ID));
    }

    public ArrayList<Product> getCart(String user) {
        ArrayList<Product> output = new ArrayList<>();
        for (Integer p : userCart.get(user)) {
            Product possibleMatch = products.get(p);
            output.add(possibleMatch); // Assume any changes to an item removes it from user's carts -- could log all changes and then when this request goes through compare it
        }
        return output;
    }

    // [start, end)
    public ArrayList<Product> returnProducts(int start, int end) {
        ArrayList<Product> output = new ArrayList<>();
        for (int i = 0; i < productsSorted.size() && i < end; i++) {
            output.add(productsSorted.get(i));
        }
        return output;
    }

    // [start, end)
    public ArrayList<Product> search(String match, int number) {
        ArrayList<Product> output = new ArrayList<>();
        for (int i = 0; i < productsSorted.size() && output.size() < number; i++) {
            if (productsSorted.get(i).getName().toLowerCase().trim().startsWith(match.toLowerCase().trim()))
                output.add(productsSorted.get(i));
        }
        return output;
    }

    public void createProduct(ProductData data, String sellerUsername, MultipartFile[] files) {

        System.out.println("Creating product...");

        Product product = new Product(data, sellerUsername);
        addProduct(product); // Maybe don't add this until all other data is saved

        int currentLine = 0;

        new File(DemoApplication.PATH + "Storage/Products/" + product.getID()).mkdirs();

        try {
            int i = 0;
            for (MultipartFile file : files) {
                if (file.getOriginalFilename() == null) {
                    System.out.println("Invalid file");
                    //Stuff
                }
                String filename = file.getOriginalFilename();
                Files.copy(file.getInputStream(), Paths.get(DemoApplication.PATH + "Storage/Products/" + product.getID() + "/" + i++ + filename.substring(filename.lastIndexOf("."))), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Successfully wrote files");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write files");
        }

        try {
            FileWriter writer = new FileWriter(file, true);
            Gson g = new Gson();

            // Write product to file
            writer.write(files.length + " " + g.toJson(product, Product.class));
            writer.write(System.getProperty("line.separator")); // new line
            writer.close();
        } catch (IOException e) {
            System.out.println("Failed to write product on line " + currentLine);
            e.printStackTrace();
        }

        System.out.println("Finished creating product");
    }

    public boolean deleteProduct(int id) {
        if (products.containsKey(id)) {
            //Write a command to not load this product - perhaps the save file can be condensed on occasion
            return true;
        }
        return false;
    }

    // Return path pointing to the next numbered file for this product
    public Path getProductNextImagePath(int ID) {
        Product product = products.get(ID);
        if (product != null) {
            int currentCount = 0;

            if (imageCount.containsKey(ID))
                currentCount = imageCount.get(ID);
            else
                new File(DemoApplication.PATH + "Storage/Products/" + ID).mkdir(); // Create Directory for this product

            imageCount.put(ID, currentCount + 1);
            writeProductImageCount(currentCount + 1, product);

            // Update product information in file

            return Paths.get(DemoApplication.PATH + "Storage/Products/" + ID, currentCount + "");
        }
        return null;
    }

    // Call after creating a new product and adding images to the product
    public void setImageCount(int ID, int amount) {
        imageCount.put(ID, amount);
        writeProductImageCount(amount, products.get(ID));
    }

    public void removeImageFromProduct(int ID, int image) {
        if (imageCount.containsKey(ID)) {
            int currentCount = imageCount.get(ID);
            if (currentCount > 0 && image < currentCount) {
                imageCount.put(ID, currentCount - 1);
                writeProductImageCount(currentCount - 1, products.get(ID));
            }
        }
    }

    private void writeProductImageCount(int newCount, Product product) {
        // Write product to file
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(productOffset.get(products.get(product).getID()));
            raf.writeInt(newCount);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Product fromID(int ID) {
        return products.get(ID);
    }
}
