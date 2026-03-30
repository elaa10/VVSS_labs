package drinkshop.service;

import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        Repository<Integer, Product> dummyRepo = new Repository<>() {
            @Override public Product save(Product entity) { return entity; }
            @Override public Product update(Product entity) { return entity; }
            @Override public Product delete(Integer id) { return null; }
            @Override public Product findOne(Integer id) { return null; }
            @Override public List<Product> findAll() { return null; }
        };
        productService = new ProductService(dummyRepo);
    }



    @Test
    @Tag("ECP")
    @DisplayName("01. TC1_ECP: Valid (Zmeura, 17.2)")
    void testTC1_ECP_Valid() {
        Product p = new Product(1, "Zmeura", 17.2, null, null);
        assertDoesNotThrow(() -> productService.addProduct(p));
    }

    @Test
    @Tag("ECP")
    @DisplayName("02. TC2_ECP: Nume sir gol")
    void testTC2_ECP_NumeSirGol() {
        Product p = new Product(1, "", 17.2, null, null);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("ECP")
    @DisplayName("03. TC3_ECP: Nume format din spatii")
    void testTC3_ECP_NumeSpatii() {
        Product p = new Product(1, " ", 17.2, null, null);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("ECP")
    @DisplayName("04. TC4_ECP: Nume null")
    void testTC4_ECP_NumeNull() {
        Product p = new Product(1, null, 17.2, null, null);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("ECP")
    @DisplayName("05. TC5_ECP: Pret negativ (-17.2)")
    void testTC5_ECP_PretNegativ() {
        Product p = new Product(1, "Zmeura", -17.2, null, null);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }


    @Test
    @Tag("BVA")
    @DisplayName("06. 02_BVA: Nume 1 caracter (Z)")
    void test02_BVA_NumeUnCaracter() {
        Product p = new Product(1, "Z", 17.2, null, null);
        assertDoesNotThrow(() -> productService.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("07. 03_BVA: Nume 254 caractere")
    void test03_BVA_Nume254() {
        String nume254 = "Z".repeat(254);
        Product p = new Product(1, nume254, 17.2, null, null);
        assertDoesNotThrow(() -> productService.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("08. 05_BVA: Pret egal cu 0")
    void test05_BVA_PretZero() {
        Product p = new Product(1, "Zmeura", 0.0, null, null);
        assertThrows(ValidationException.class, () -> productService.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("09. 06_BVA: Pret minim valid (0.01)")
    void test06_BVA_PretMinim() {
        Product p = new Product(1, "Zmeura", 0.01, null, null);
        assertDoesNotThrow(() -> productService.addProduct(p));
    }
}