package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemMergeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry.GetRecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Pantry;
import at.ac.tuwien.sepr.groupphase.backend.entity.PantryItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PantryRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
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
public class PantryServiceImpl implements PantryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final PantryItemRepository pantryItemRepository;
    private final PantryRepository pantryRepository;
    private final ItemService itemService;
    private final RecipeRepository recipeRepository;
    private final ItemMapper itemMapper;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional
    public List<PantryItem> findAllItems(long pantryId) {
        LOGGER.debug("Find all pantryItem in pantry with id {}", pantryId);
        Optional<Pantry> pantry = pantryRepository.findById(pantryId);
        if (pantry.isPresent()) {
            LOGGER.debug("Found pantry: {}", pantry.get());
            return pantry.get().getItems();
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public List<PantryItem> findItemsByDescription(String description, long pantryId) {
        LOGGER.debug("Find all pantryItem in pantry with id {} matching the description \"{}\"", pantryId, description);
        Optional<Pantry> pantry = pantryRepository.findById(pantryId);
        if (pantry.isPresent()) {
            LOGGER.debug("Found pantry: {}", pantry.get());
            return pantryItemRepository.findByDescriptionContainingIgnoreCaseAndPantryIsOrderById(description, pantry.get());
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public Item addItemToPantry(PantryItem item, long pantryId) {
        LOGGER.debug("Add pantryItem {} to pantry with ID {}", item, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            return itemService.pantryAutoMerge(item, pantry);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public void deleteItem(long pantryId, long itemId) {
        LOGGER.debug("Delete pantryItem {} in pantry with ID {}", itemId, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            PantryItem item = pantryItemRepository.getReferenceById(itemId);
            pantry.removeItem(item);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public PantryItem updateItem(PantryItemDto item, long pantryId) {
        LOGGER.debug("Update pantryItem {} in pantry with ID {}", item, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            PantryItem updatedItem = PantryItem.builder()
                .pantry(pantry)
                .id(item.getId())
                .unit(item.getUnit())
                .amount(item.getAmount())
                .description(item.getDescription())
                .lowerLimit(item.getLowerLimit())
                .build();
            return itemRepository.save(updatedItem);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public PantryItem mergeItems(PantryItemMergeDto itemMergeDto, long pantryId) throws ConflictException {
        if (itemMergeDto.getItemToDeleteId().equals(itemMergeDto.getResult().getId())) {
            throw new ConflictException("Merging Error", List.of("Can not merge item with itself"));
        }
        deleteItem(pantryId, itemMergeDto.getItemToDeleteId());
        return updateItem(itemMergeDto.getResult(), pantryId);
    }

    @Override
    public List<RecipeListDto> getRecipes(Long pantryId) {
        return recipeMapper.recipeEntityListToListOfRecipeListDto(recipeRepository.findRecipeByPantry(pantryId));
    }

    @Override
    public List<RecipeListDto> getRecipes(GetRecipeDto getRecipeDto) {
        return recipeMapper.recipeEntityListToListOfRecipeListDto(recipeRepository.findRecipesByItemIds(getRecipeDto.getItemIds()));
    }

    @Override
    @Transactional
    public List<String> removeRecipeIngredientsFromPantry(long groupId, long recipeId, int portion) {
        List<PantryItem> pantryItems = pantryItemRepository.findMatchingRecipeItemsInPantry(groupId, recipeId);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        List<Item> recipeItems = optionalRecipe.get().getIngredients();
        double ratio = (double) portion / optionalRecipe.get().getPortionSize();
        List<String> changedItems = new ArrayList<>();
        Item currentRecipeItem;
        for (PantryItem pantryItem : pantryItems) {
            Optional<Item> optional = recipeItems.stream()
                .filter(o ->
                    o.getDescription().equals(pantryItem.getDescription())
                        && o.getUnit().equals(pantryItem.getUnit())).findFirst();
            if (optional.isPresent()) {
                currentRecipeItem = optional.get();
                //If Amount would be negative set it to 0, otherwise reduce amount by recipe amount * ratio
                pantryItem.setAmount(Math.max((int) Math.ceil(pantryItem.getAmount() - (currentRecipeItem.getAmount() * ratio)), 0));
                changedItems.add(pantryItem.getDescription());
                updateItem(itemMapper.pantryItemToPantryItemDto(pantryItem), groupId);

            }
        }

        return changedItems;
    }

}
