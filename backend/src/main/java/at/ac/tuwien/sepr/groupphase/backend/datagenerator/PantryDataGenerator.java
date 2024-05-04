package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class PantryDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PantryRepository pantryRepository;

    public PantryDataGenerator(PantryRepository pantryRepository) {
        this.pantryRepository = pantryRepository;
    }

    @PostConstruct
    private void generatePantryWithItems() {
        if (pantryRepository.findAll().size() > 0) {
            LOGGER.debug("pantry already generated");
        } else {
            LOGGER.debug("generating 1 pantry entry");

            Pantry pantry = Pantry.builder().build();
            Item item = Item.builder().description("Milk").unit(Unit.Milliliter).amount(500).build();
            Item item2 = Item.builder().description("Chocolate").unit(Unit.Gram).amount(200).build();
            pantry.addItem(item);
            pantry.addItem(item2);
            LOGGER.debug("saving pantry {}", pantry);
            pantryRepository.save(pantry);
        }
    }
}
