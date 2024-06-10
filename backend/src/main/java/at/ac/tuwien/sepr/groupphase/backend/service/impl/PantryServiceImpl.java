package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RemoveIngredientsFromPantryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.pantryitem.PantryItemMergeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.pantry.GetRecipeDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeByItemsDto;
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
import at.ac.tuwien.sepr.groupphase.backend.service.PantryItemService;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PantryServiceImpl implements PantryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemRepository itemRepository;
    private final PantryItemRepository pantryItemRepository;
    private final PantryRepository pantryRepository;
    private final PantryItemService pantryItemService;
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
            return pantryItemService.pantryAutoMerge(item, pantry);
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
    public Item updateItem(PantryItem item, long pantryId) {
        LOGGER.debug("Update pantryItem {} in pantry with ID {}", item, pantryId);
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            item.setPantry(pantry);
            return pantryItemService.pantryAutoMerge(item, pantry);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }

    @Override
    @Transactional
    public List<Item> updateItems(List<PantryItem> items, long pantryId) {
        List<Item> result = new ArrayList<>();
        for (PantryItem item : items) {
            result.add(updateItem(item, pantryId));
        }
        return result;
    }

    @Override
    @Transactional
    public Item mergeItems(PantryItemMergeDto itemMergeDto, long pantryId) throws ConflictException {
        if (itemMergeDto.getItemToDeleteId().equals(itemMergeDto.getResult().getId())) {
            throw new ConflictException("Merging Error", List.of("Can not merge item with itself"));
        }
        deleteItem(pantryId, itemMergeDto.getItemToDeleteId());
        Optional<Pantry> optionalPantry = pantryRepository.findById(pantryId);
        if (optionalPantry.isPresent()) {
            Pantry pantry = optionalPantry.get();
            PantryItem updatedItem = PantryItem.builder()
                .pantry(pantry)
                .id(itemMergeDto.getResult().getId())
                .unit(itemMergeDto.getResult().getUnit())
                .amount(itemMergeDto.getResult().getAmount())
                .description(itemMergeDto.getResult().getDescription())
                .lowerLimit(itemMergeDto.getResult().getLowerLimit())
                .build();
            return itemRepository.save(updatedItem);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", pantryId));
        }
    }


    @Override
    @Transactional
    public List<RecipeByItemsDto> getRecipes(GetRecipeDto getRecipeDto, Long pantryId, Long userId) {

        Pantry pantry = pantryRepository.getReferenceById(pantryId);
        List<PantryItem> pantryItems = pantry.getItems();
        List<Recipe> recipes = recipeRepository.findRecipesByItemIds(getRecipeDto.getItemIds(), userId);

        List<RecipeByItemsDto> recipeByItemsDtoList = new ArrayList<>();

        for (Recipe recipe : recipes) {
            recipeByItemsDtoList.add(RecipeByItemsDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .ingredients(itemMapper.listOfItemsToListOfItemDto(recipe.getIngredients()))
                .itemsInPantry(itemMapper.listOfPantryItemsToListOfPantryItemDto(pantryItems
                    .stream()
                    .filter(p -> recipe.getIngredients()
                        .stream()
                        .anyMatch(r -> p.getDescription().equals(r.getDescription()) && p.getUnit().equals(r.getUnit()))).collect(Collectors.toList())))
                .build());
        }

        recipeByItemsDtoList.sort(new Comparator<RecipeByItemsDto>() {
            @Override
            public int compare(RecipeByItemsDto o1, RecipeByItemsDto o2) {
                float ratio1 = (float) o1.getItemsInPantry().size() / o1.getIngredients().size();
                float ratio2 = (float) o2.getItemsInPantry().size() / o2.getIngredients().size();
                if (ratio2 - ratio1 == 0) {
                    return o2.getItemsInPantry().size() > o1.getItemsInPantry().size() ? 1 : -1;
                }
                return (int) ((ratio2 - ratio1) * 100);
            }
        });
        return recipeByItemsDtoList;
    }

    @Override
    @Transactional
    public RemoveIngredientsFromPantryDto removeRecipeIngredientsFromPantry(long pantryId, long recipeId, int portion) {
        //Get Recipe
        List<Item> recipe = recipeRepository.findAllIngredientsByRecipeId(recipeId);

        //Get Pantry
        List<PantryItem> pantryItems = new ArrayList<>();
        if (pantryId != -1L) {
            pantryItems = pantryItemRepository.findMatchingRecipeItemsInPantry(pantryId, recipeId);
        }

        List<PantryItem> result = new ArrayList<>();
        for (PantryItem item : pantryItems) {
            if (recipe.stream().anyMatch(o -> item.getDescription().equals(o.getDescription())
                && item.getUnit().equals(o.getUnit()))) {
                
                result.add(item);
            }
        }

        return RemoveIngredientsFromPantryDto.builder().recipeItems(recipe).pantryItems(result).build();
    }

    @Override
    @Transactional
    public List<PantryItemDto> findAllMissingItems(long pantryId) {
        var item = pantryItemRepository.findAllMissingItems(pantryId);
        var itemList = itemMapper.listOfPantryItemsToListOfPantryItemDto(item);
        //Change quantity to the missing quantity
        for (PantryItemDto pantryItem : itemList) {
            pantryItem.setAmount((int) (pantryItem.getLowerLimit() - pantryItem.getAmount()));
        }
        return itemList;
    }

}
