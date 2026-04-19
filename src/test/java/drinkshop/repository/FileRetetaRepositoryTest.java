package drinkshop.repository;

import drinkshop.domain.Reteta;
import drinkshop.repository.file.FileRetetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileRetetaRepositoryTest {
    private static class TestableRepo extends FileRetetaRepository {
        public TestableRepo() {
            super("dummy.txt");
        }
        @Override
        protected void loadFromFile() {
        }

        @Override
        public Reteta extractEntity(String line) {
            return super.extractEntity(line);
        }
    }

    private TestableRepo repo;

    @BeforeEach
    void setUp() {
        repo = new TestableRepo();
    }

    @Test
    void testLineNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repo.extractEntity(null));
        assertEquals("Line is null or empty", ex.getMessage());
    }

    @Test
    void testLineEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repo.extractEntity(""));
        assertEquals("Line is null or empty", ex.getMessage());
    }

    @Test
    void testNoIngredients() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repo.extractEntity("1"));
        assertEquals("Invalid format: no ingredients", ex.getMessage());
    }

    @Test
    void testInvalidIngredientFormat() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repo.extractEntity("1,faraSeparator"));
        assertEquals("Invalid ingredient format", ex.getMessage());
    }

    @Test
    void testEmptyIngredientName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> repo.extractEntity("1,:100.0"));
        assertEquals("Ingredient name is empty", ex.getMessage());
    }

    @Test
    void testValidOneIngredient() {
        Reteta r = repo.extractEntity("1,faina:200.0");
        assertEquals(1, r.getId());
        assertEquals(1, r.getIngrediente().size());
        assertEquals("faina", r.getIngrediente().get(0).getDenumire());
        assertEquals(200.0, r.getIngrediente().get(0).getCantitate());
    }

    @Test
    void testValidMultipleIngredients() {
        Reteta r = repo.extractEntity("2,faina:200.0,zahar:100.0,oua:3.0");
        assertEquals(2, r.getId());
        assertEquals(3, r.getIngrediente().size());
        assertEquals("zahar", r.getIngrediente().get(1).getDenumire());

    }
}
