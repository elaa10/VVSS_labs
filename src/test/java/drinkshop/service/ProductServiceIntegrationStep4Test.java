package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab04 - Step 4: Integration Testing (top-down, breadth-first) - OPTIONAL.
 *
 * Pas opțional conform cerinței: la breadth-first, Step 4 e necesar doar daca
 * la pasii anteriori s-au folosit mock-uri pentru E. La noi Product este real
 * inca de la Step 3, dar includem totusi un test end-to-end care exerseaza
 * intreg lantul S + V + R + E pentru completitudine.
 */
public class ProductServiceIntegrationStep4Test {

    static class InMemoryProductRepository extends AbstractRepository<Integer, Product> {
        @Override
        protected Integer getId(Product entity) {
            return entity.getId();
        }
    }

    private ProductService service;

    @BeforeEach
    void setUp() {
        Repository<Integer, Product> repo = new InMemoryProductRepository();
        ProductValidator validator = new ProductValidator();
        service = new ProductService(repo, validator);
    }

    @Test
    @DisplayName("Step 4 - flux complet: adaug, actualizez, sterg cu Product real")
    void fullFlow_addUpdateDelete() {
        // Arrange + Act: adaugare
        Product p = new Product(1, "Mocha", 17.0,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY);
        service.addProduct(p);
        assertEquals(1, service.getAllProducts().size());

        // Act: actualizare
        service.updateProduct(1, "Mocha XL", 22.0,
                CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY);
        Product updated = service.findById(1);
        assertNotNull(updated);
        assertEquals("Mocha XL", updated.getNume());
        assertEquals(22.0, updated.getPret(), 0.001);

        // Act: stergere
        service.deleteProduct(1);
        assertTrue(service.getAllProducts().isEmpty());
    }

    @Test
    @DisplayName("Step 4 - filtrare combinata pe categorie si tip cu produse reale")
    void combinedFiltering_onRealProducts() {
        // Arrange
        service.addProduct(new Product(1, "Espresso", 8.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC));
        service.addProduct(new Product(2, "Soy Latte", 14.0,
                CategorieBautura.MILK_COFFEE, TipBautura.PLANT_BASED));
        service.addProduct(new Product(3, "Lemonade", 10.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED));

        // Act
        List<Product> plantBased = service.filterByTip(TipBautura.PLANT_BASED);
        List<Product> allTips = service.filterByTip(TipBautura.ALL);

        // ASSERT
        assertEquals(1, plantBased.size());
        assertEquals("Soy Latte", plantBased.get(0).getNume());
        assertEquals(3, allTips.size());
    }
}
