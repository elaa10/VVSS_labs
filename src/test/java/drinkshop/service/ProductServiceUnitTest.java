package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Lab04 - Step 1: Unit Testing pentru ProductService (clasa S).
 *
 * Scenariu ales: (1) V <--- S ---> R, integrare top-down breadth-first.
 *
 * In acest pas, S (ProductService) este testat IZOLAT.
 * Cele doua dependinte directe (V = ProductValidator si R = Repository<Integer, Product>)
 * sunt inlocuite cu obiecte MOCK construite cu Mockito.
 *
 * NOTA: ProductService instantiaza intern `new ProductValidator()` in metoda addProduct().
 * Pentru a putea controla complet validatorul ca mock pur, vom folosi un constructor de
 * tip "package-private" care primeste un Validator extern. Daca implementarea ta nu il are
 * inca, vezi nota din README despre cum se refactorizeaza ProductService pentru testabilitate.
 *
 * Fiecare test foloseste atat ASSERT cat si VERIFY, conform cerintei din Lab04.
 */
public class ProductServiceUnitTest {

    private Repository<Integer, Product> repoMock;
    private drinkshop.service.validator.Validator<Product> validatorMock;
    private ProductService service;

    @BeforeEach
    void setUp() {
        // Cream MOCK-uri pentru cele doua colaboratori
        repoMock = mock(Repository.class);
        validatorMock = mock(drinkshop.service.validator.Validator.class);

        // Injectam mock-urile in serviciu (constructor de test)
        service = new ProductService(repoMock, validatorMock);
    }

    @Test
    @DisplayName("Step 1 - addProduct cu produs valid: salveaza in repo si apeleaza validatorul")
    void addProduct_valid_callsValidatorAndSave() {
        // Arrange
        Product p = new Product(1, "Espresso", 8.5,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        when(repoMock.save(p)).thenReturn(p);

        // Act
        service.addProduct(p);

        // VERIFY: validatorul a fost apelat exact o data, cu produsul nostru
        verify(validatorMock, times(1)).validate(p);
        // VERIFY: repository-ul a primit save() exact o data
        verify(repoMock, times(1)).save(p);
        // VERIFY: nu s-au mai facut alte apeluri pe repo
        verifyNoMoreInteractions(repoMock);
    }

    @Test
    @DisplayName("Step 1 - addProduct cu produs invalid: NU se face save in repo")
    void addProduct_invalid_throwsAndDoesNotSave() {
        // Arrange: produs invalid (pret negativ) - simulam ca validatorul arunca
        Product p = new Product(1, "InvalidDrink", -5.0,
                CategorieBautura.JUICE, TipBautura.BASIC);
        doThrow(new ValidationException("Pret invalid!"))
                .when(validatorMock).validate(p);

        // Act + ASSERT: se arunca ValidationException
        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.addProduct(p));
        assertTrue(ex.getMessage().contains("Pret"));

        // VERIFY: validatorul s-a apelat o data
        verify(validatorMock, times(1)).validate(p);
        // VERIFY: repo.save() NU a fost apelat niciodata
        verify(repoMock, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Step 1 - getAllProducts deleaga catre repository")
    void getAllProducts_returnsListFromRepo() {
        // Arrange
        Product p1 = new Product(1, "Latte", 12.0,
                CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        Product p2 = new Product(2, "Mojito", 18.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);
        List<Product> stub = Arrays.asList(p1, p2);
        when(repoMock.findAll()).thenReturn(stub);

        // Act
        List<Product> result = service.getAllProducts();

        // ASSERT
        assertEquals(2, result.size());
        assertEquals("Latte", result.get(0).getNume());
        // VERIFY
        verify(repoMock, times(1)).findAll();
    }

    @Test
    @DisplayName("Step 1 - deleteProduct apeleaza repo.delete cu id-ul corect")
    void deleteProduct_callsRepoDelete() {
        // Act
        service.deleteProduct(42);

        // VERIFY
        verify(repoMock, times(1)).delete(42);
        // VERIFY: la stergere NU se apeleaza validatorul deloc
        verify(validatorMock, never()).validate(any(Product.class));
    }
}
