package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class RecipeDataGenerator implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    UserRepository userRepository;
    UserService userService;
    ItemRepository itemRepository;
    RecipeRepository recipeRepository;

    @Override
    @Transactional
    public void generateData() {
        LOGGER.debug("generating data for recipes");
        ApplicationUser owner = userRepository.save(ApplicationUser.builder().email("RecipeGenerator@test.at").password("RezeptTest1").admin(false).build());


        Recipe recipe1 = Recipe.builder()
            .name("Lasagne Bolognese")
            .description("Zuerst die Bolognesesauce zubereiten: Dafür Zwiebeln und Knoblauch schälen "
                + "und fein hacken. Die Karotten schälen und raspeln. Dann Öl in einem Topf erhitzen, Faschiertes"
                + " darin gleichmäßig anrösten und Zwiebeln, Knoblauch und Karotten zugeben und weiter anbraten. "
                + "Tomaten und Tomatenmark sowie das Ketchup, Basilikum, Oregano, Thymian, Salz und Pfeffer "
                + "hinzugeben und auf kleinster Stufe ca. 10 Min. köcheln lassen.\n"
                + "Anschließend die Bechamelsauce herstellen: Dafür Butter in einem Topf zerlassen, Mehl zufügen "
                + "und sofort mit einem Schneebesen umrühren. Jetzt ganz langsam unter ständigem Rühren die Milch "
                + "zufügen. Langsam aufkochen und rühren bis die Sauce dicklich ist. Sodann mit Pfeffer, Salz und"
                + "Muskat würzen.\n"
                + "Für die Lasagne eine Auflaufform mit Olivenöl einfetten. Nun abwechselnd die Lasagneblätter und "
                + "Bolognesesauce einschichten. Mit den Lasagneblättern beginnen, danach die Sauce usw. abwechselnd schichten.\n"
                + "Ganz zum Schluss mit der Bechamelsauce abschließen und frisch geriebenen Gouda draufgeben. "
                + "Im vorgeheizten Ofen bei 180° C Heißluft ca. 30 Minuten backen.")
            .isPublic(true)
            .owner(owner)
            .portionSize(1).likes(1).dislikes(1).build();
        owner.addRecipe(recipe1);

        List<Item> items = itemRepository.findAll();

        for (Item item : items.subList(0, 13)) {
            recipe1.addIngredient(item);
        }
        recipeRepository.saveAndFlush(recipe1);

        Recipe recipe2 = Recipe.builder()
            .name("PaprikaHuhn")
            .description("Das Huhn waschen, die Flügel vom Huhn abtrennen, den Rest in 4 Teile "
                + "zerlegen und mit Salz und Pfeffer würzen. Die Flügel nicht wegwerfen, diese können"
                + " für eine Hühnersuppe verwendet werden.\n"
                + "Danach die Zwiebel und den Knoblauch schälen und sehr fein würfeln. Das Öl in einer "
                + "großen Pfanne erhitzen, Zwiebeln und Knoblauch darin goldgelb braten und vom Herd nehmen. "
                + "Nun Paprikapulver und das Huhn dazugeben. Anschließend mit der Hühnersuppe auffüllen und "
                + "zugedeckt 15 Minuten dünsten lassen, dabei öfter umrühren.\n"
                + "Dann die gewaschenen und in Streifen geschnittenen Paprikaschoten und Tomatenstücke "
                + "zugeben und bei schwacher Hitze in etwa 25-30 Minuten weich garen.\n"
                + "Anschließend das Hühnerfleisch aus der Suppe nehmen und von Haut und Knochen lösen. "
                + "Die Knochen können weggeworfen werden. Nun den Schlagobers und den Sauerrahm einrühren, "
                + "mit dem Pürierstab pürieren und mit Salz und Pfeffer nochmals abschmecken. Zum Schluss nochmals"
                + " das ausgelöste Hühnerfleisch hinzufügen und ganz kurz aufkochen lassen.")
            .isPublic(false)
            .owner(owner)
            .portionSize(1).likes(1).dislikes(1).build();
        owner.addRecipe(recipe2);
        List<Item> ingredientsToAdd2 = items.subList(13, 25);
        for (Item item : ingredientsToAdd2) {
            recipe2.addIngredient(item);
        }
        recipeRepository.saveAndFlush(recipe2);
        userRepository.saveAndFlush(owner);


        recipeRepository.saveAndFlush(Recipe.builder()
            .name("Empty Recipe")
            .description("This Recipe has no Ingredients")
            .isPublic(true)
            .portionSize(1)
            .owner(owner)
            .ingredients(new ArrayList<>())
            .likes(0).dislikes(0).build());
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for recipes");
        recipeRepository.deleteAll();
    }
}