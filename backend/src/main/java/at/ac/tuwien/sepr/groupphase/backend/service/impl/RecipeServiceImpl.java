package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, ItemRepository itemRepository, RecipeMapper recipeMapper, ItemMapper itemMapper) {
        this.recipeRepository = recipeRepository;
        this.itemRepository = itemRepository;
        this.recipeMapper = recipeMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public List<Item> findAllIngredients(long recipeId) {
        LOGGER.debug("Find all ingredients in recipe with id {}", recipeId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isPresent()) {
            LOGGER.debug("Found recipe: {}", recipe.get());
            return recipe.get().getIngredients();
        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", recipeId));
        }
    }

    @Override
    @Transactional
    public String getName(long recipeId) {
        LOGGER.debug("Finding name for recipe with ID {}", recipeId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isPresent()) {
            LOGGER.debug("Found recipe: {}", recipe.get());
            return recipe.get().getName();
        } else {
            throw new NotFoundException(String.format("Could not find recipe with ID %s", recipeId));
        }
    }

    @Override
    @Transactional
    public String getDescription(long recipeId) {
        LOGGER.debug("Finding description for ID {}", recipeId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isPresent()) {
            LOGGER.debug("Found description: {}", recipe.get());
            return recipe.get().getDescription();
        } else {
            throw new NotFoundException(String.format("Could not find description with ID %s", recipeId));
        }
    }

    @Override
    @Transactional
    public boolean getIsPublic(long recipeId) {
        LOGGER.debug("Finding isPublic for recipe with ID {}", recipeId);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isPresent()) {
            LOGGER.debug("Found recipe: {}", recipe.get());
            return recipe.get().isPublic();
        } else {
            throw new NotFoundException(String.format("Could not find recipe with ID %s", recipeId));
        }
    }

    @Override
    @Transactional
    public List<Item> findItemsByDescription(String description, long recipeId) {
        LOGGER.debug("Find all items in recipe with id {} matching the description \"{}\"", recipeId, description);
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isPresent()) {
            LOGGER.debug("Found recipe: {}", recipe.get());
            return itemRepository.findByDescriptionContainingIgnoreCaseAndRecipeIsOrderById(description, recipe.get());
        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", recipeId));
        }
    }

    @Override
    @Transactional
    public Item addItemToRecipe(Item item, long recipeId) {
        LOGGER.debug("Add item {} to recipe with ID {}", item, recipeId);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.addIngredient(item);
            return itemRepository.save(item);
        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", recipeId));
        }
    }

    @Override
    @Transactional
    public void deleteItem(long recipeId, long itemId) {
        LOGGER.debug("Delete item {} in recipe with ID {}", itemId, recipeId);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            Item item = itemRepository.getReferenceById(itemId);
            recipe.removeItem(item);
        }
    }

    @Override
    public Item updateItem(ItemDto item, long recipeId) {
        LOGGER.debug("Update item {} in recipe with ID {}", item, recipeId);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            Item loadItem = itemRepository.getReferenceById(item.getId());
            loadItem = Item.builder()
                .recipe(recipe)
                .id(item.getId())
                .unit(item.getUnit())
                .amount(item.getAmount())
                .description(item.getDescription()).build();
            itemRepository.save(loadItem);
            return loadItem;
        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", recipeId));
        }
    }

    @Override
    @Transactional
    public RecipeDetailDto createRecipe(RecipeCreateDto recipe) {
        LOGGER.debug("Create recipe {}", recipe);
        List<Item> ingredients = itemMapper.listOfItemCreateDtoToListOfItemEntity(recipe.getIngredients());
        recipe.setIngredients(new ArrayList<>());

        Recipe finishedRecipe = recipeRepository.save(recipeMapper.recipeCreateToRecipeEntity(recipe));

        for (Item ingredient : ingredients) {
            if (ingredient != null) {
                addItemToRecipe(ingredient, finishedRecipe.getId());
            }
        }

        return recipeMapper.recipeEntityToRecipeDetailDto(finishedRecipe);
    }

    @Override
    @Transactional
    public RecipeDetailDto getById(long id) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (optionalRecipe.isPresent()) {
            return recipeMapper.recipeEntityToRecipeDetailDto(optionalRecipe.get());
        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", id));
        }
    }
}