package drinkshop.repository.file;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileRetetaRepository
        extends FileAbstractRepository<Integer, Reteta> {

    public FileRetetaRepository(String fileName) {
        super(fileName);
        loadFromFile();
    }

    @Override
    protected Integer getId(Reteta entity) {
        return entity.getId();
    }

    @Override
    protected Reteta extractEntity(String line) {
        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("Line is null or empty");
        }

        String[] elems = line.split(",");

        if (elems.length < 2) {
            throw new IllegalArgumentException("Invalid format: no ingredients");
        }

        int productId = Integer.parseInt(elems[0]);
        List<IngredientReteta> ingrediente = new ArrayList<>();
        int index = 1;

        while (index < elems.length) {
            String ingredientTotal = elems[index++];
            String[] ingredientSeparat = ingredientTotal.split(":");

            if (ingredientSeparat.length != 2) {
                throw new IllegalArgumentException("Invalid ingredient format");
            }

            String ingredientName = ingredientSeparat[0];

            if (ingredientName.isEmpty()) {
                throw new IllegalArgumentException("Ingredient name is empty");
            }

            Double ingredientQuantity = Double.parseDouble(ingredientSeparat[1]);
            ingrediente.add(new IngredientReteta(ingredientName, ingredientQuantity));
        }

        return new Reteta(productId, ingrediente);
    }

    @Override
    protected String createEntityAsString(Reteta entity) {
        String ingrediente = entity.getIngrediente().stream()
                        .map(entry -> entry.getDenumire() + ":" + entry.getCantitate())
                        .collect(Collectors.joining(","));
        return entity.getId() + "," +
                ingrediente;
    }
}
