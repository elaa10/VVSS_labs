package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Lab04 - Step 2: Integration Testing (top-down, breadth-first).
 *
 * S (ProductService) + V (ProductValidator REAL) sunt testate impreuna.
 * R (Repository) ramane MOCK.
 *
 * Verificam ca validatorul real respinge produsele invalide si lasa sa treaca
 * cele valide pana la repository.
 */
public class ProductServiceIntegrationStep2Test {

    private Repository<Integer, Product> repoMock;
    private ProductValidator validatorReal; // REAL acum, nu mock
    private ProductService service;

    @BeforeEach
    void setUp() {
        repoMock = mock(Repository.class);
        validatorReal = new ProductValidator(); // V este REAL
        service = new ProductService(repoMock, validatorReal);
    }

    @Test
    @DisplayName("Step 2 - produs valid trece de validator si se salveaza")
    void addProduct_validPassesRealValidator_andIsSaved() {
        // Arrange
        Product valid = new Product(10, "Cappuccino", 14.5,
                CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        when(repoMock.save(valid)).thenReturn(valid);

        // Act
        assertDoesNotThrow(() -> service.addProduct(valid));

        // VERIFY: a ajuns la repo cu acelasi produs
        verify(repoMock, times(1)).save(valid);
    }

    @Test
    @DisplayName("Step 2 - produs cu nume gol este respins de validatorul real")
    void addProduct_emptyName_isRejectedByRealValidator() {
        // Arrange
        Product invalid = new Product(11, "", 10.0,
                CategorieBautura.TEA, TipBautura.WATER_BASED);

        // Act + ASSERT
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addProduct(invalid));
        assertTrue(ex.getMessage().toLowerCase().contains("nume"));

        // VERIFY: repo NU a fost atins
        verify(repoMock, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Step 2 - produs cu id<=0 si pret<=0 acumuleaza mai multe erori")
    void addProduct_multipleErrors_areAggregated() {
        // Arrange
        Product invalid = new Product(0, "X", -1.0,
                CategorieBautura.JUICE, TipBautura.BASIC);

        // Act + ASSERT
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addProduct(invalid));
        assertTrue(ex.getMessage().contains("ID"));
        assertTrue(ex.getMessage().contains("Pret"));

        // VERIFY
        verify(repoMock, never()).save(any(Product.class));
    }
}
