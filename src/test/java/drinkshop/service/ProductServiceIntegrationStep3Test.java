package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab04 - Step 3: Integration Testing (top-down, breadth-first).
 *
 * S + V + R sunt toate REALE.
 * Folosim un repository in-memory bazat pe AbstractRepository (fara fisier,
 * pentru a evita I/O in teste). E (Product) este folosit ca obiect real -
 * la breadth-first nu e nevoie obligatoriu de mock pentru E.
 */
public class ProductServiceIntegrationStep3Test {

    /**
     * Repository in-memory real (fara persistenta pe disc).
     * Mosteneste AbstractRepository, deci salveaza intr-un HashMap.
     */
    static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }

    private Repository<Integer, Product> repoReal;
    private ProductValidator validatorReal;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repoReal = new InMemoryProductRepository(); // R REAL
        validatorReal = new ProductValidator();     // V REAL
        service = new ProductService(repoReal, validatorReal);
    }

    @Test
    @DisplayName("Step 3 - addProduct valid persista in repo si poate fi regasit")
    void addProduct_valid_isStoredAndRetrievable() {
        // Arrange
        Product p = new Product(1, "Matcha Latte", 16.0,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.PLANT_BASED);

        // Act
        service.addProduct(p);

        // ASSERT pe starea repo-ului real
        List<Product> all = service.getAllProducts();
        assertEquals(1, all.size());
        assertEquals("Matcha Latte", all.get(0).getNume());
        assertEquals(p, service.findById(1));
    }

    @Test
    @DisplayName("Step 3 - addProduct invalid nu ajunge in repo")
    void addProduct_invalid_doesNotReachRepo() {
        // Arrange
        Product invalid = new Product(2, "  ", 10.0,
                CategorieBautura.TEA, TipBautura.WATER_BASED);

        // Act + ASSERT
        assertThrows(ValidationException.class, () -> service.addProduct(invalid));
        // ASSERT pe starea reala: repo este gol
        assertTrue(service.getAllProducts().isEmpty());
    }

    @Test
    @DisplayName("Step 3 - filtrare dupa categorie functioneaza corect cu repo real")
    void filterByCategorie_returnsOnlyMatching() {
        // Arrange
        service.addProduct(new Product(1, "Espresso", 8.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC));
        service.addProduct(new Product(2, "Latte", 12.0,
                CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
        service.addProduct(new Product(3, "Americano", 9.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC));

        // Act
        List<Product> classic = service.filterByCategorie(CategorieBautura.CLASSIC_COFFEE);

        // ASSERT
        assertEquals(2, classic.size());
        assertTrue(classic.stream().allMatch(
                p -> p.getCategorie() == CategorieBautura.CLASSIC_COFFEE));
    }

    @Test
    @DisplayName("Step 3 - deleteProduct elimina produsul din repo")
    void deleteProduct_removesFromRepo() {
        // Arrange
        Product p = new Product(5, "Iced Latte", 15.0,
                CategorieBautura.ICED_COFFEE, TipBautura.DAIRY);
        service.addProduct(p);
        assertEquals(1, service.getAllProducts().size());

        // Act
        service.deleteProduct(5);

        // ASSERT
        assertTrue(service.getAllProducts().isEmpty());
        assertNull(service.findById(5));
    }
}
