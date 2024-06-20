package at.ac.tuwien.sepr.groupphase.backend.service.impl;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.item.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeDetailWithUserInfoDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.recipe.RecipeGlobalListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.RecipeMapper;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.AlreadyRatedException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final UserRepository userRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, ItemRepository itemRepository, RecipeMapper recipeMapper, ItemMapper itemMapper, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.itemRepository = itemRepository;
        this.recipeMapper = recipeMapper;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
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
    public Item updateItem(ItemDto item, long recipeId) {
        LOGGER.debug("Update pantryItem {} in pantry with ID {}", item, recipeId);
        Optional<Recipe> optionalPantry = recipeRepository.findById(recipeId);
        if (optionalPantry.isPresent()) {
            Recipe recipe = optionalPantry.get();
            Item updatedItem = Item.builder()
                .recipe(recipe)
                .id(item.getId())
                .unit(item.getUnit())
                .amount(item.getAmount())
                .description(item.getDescription())
                .build();
            return itemRepository.save(updatedItem);
        } else {
            throw new NotFoundException(String.format("Could not find pantry with id %s", recipeId));
        }
    }

    @Override
    @Transactional
    public void deleteItem(long recipeId, long itemId) {
        LOGGER.debug("Delete item {} in recipe with ID {}", itemId, recipeId);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            Item ingredient = itemRepository.getReferenceById(itemId);
            recipe.removeItem(ingredient);

            recipeRepository.save(recipe);

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

        userRepository.save(finishedRecipe.getOwner().addRecipe(finishedRecipe));

        return recipeMapper.recipeEntityToRecipeDetailDto(finishedRecipe);
    }

    @Override
    @Transactional
    public RecipeDetailDto getById(long id) {
        LOGGER.debug("Get by Id: {}", id);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (optionalRecipe.isPresent()) {
            return recipeMapper.recipeEntityToRecipeDetailDto(optionalRecipe.get());
        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", id));
        }
    }

    @Override
    public RecipeDetailWithUserInfoDto getByIdWithInfo(long id, ApplicationUser user) {
        user = userRepository.findApplicationUserByIdWithLikeInfo(user.getId());
        LOGGER.debug("Get by Id with info: {}", id);
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe result = optionalRecipe.get();
            return RecipeDetailWithUserInfoDto.builder()
                .id(result.getId())
                .name(result.getName())
                .description(result.getDescription())
                .isPublic(result.getIsPublic())
                .likes(result.getLikes())
                .dislikes(result.getDislikes())
                .portionSize(result.getPortionSize())
                .owner(result.getOwner())
                .ingredients(itemMapper.listOfItemsToListOfItemDto(result.getIngredients()))
                .likedByUser(user.getLikedRecipes().stream().anyMatch(o -> o.getId().equals(id)))
                .dislikedByUser(user.getDislikedRecipes().stream().anyMatch(o -> o.getId().equals(id)))
                .build();

        } else {
            throw new NotFoundException(String.format("Could not find recipe with id %s", id));
        }
    }

    @Override
    @Transactional
    public List<RecipeListDto> getRecipesFromUser() {
        return recipeMapper.recipeEntityListToListOfRecipeListDto(recipeRepository.findAll());
    }

    @Override
    @Transactional
    public RecipeDetailDto updateRecipe(RecipeDetailDto toUpdate) {
        LOGGER.debug("Update Recipe with ID {}", toUpdate.getId());
        Optional<Recipe> optional = recipeRepository.findById(toUpdate.getId());
        if (optional.isPresent()) {
            Recipe fillInRecipe = optional.get();
            toUpdate.setOwner(fillInRecipe.getOwner());
            toUpdate.setDislikedByUsers(fillInRecipe.getDislikedByUsers());
            toUpdate.setLikedByUsers(fillInRecipe.getLikedByUsers());
            return recipeMapper.recipeEntityToRecipeDetailDto(recipeRepository.save(recipeMapper.recipeDetailDtoToRecipeEntity(toUpdate)));
        } else {
            throw new NotFoundException("Could not find recipe to update");
        }
    }

    @Override
    @Transactional
    public List<RecipeGlobalListDto> getPublicRecipeOrderedByLikes(ApplicationUser user) {
        LOGGER.debug("Get all public recipes");
        user = userRepository.findApplicationUserByIdWithLikeInfo(user.getId());
        List<RecipeListDto> listDtos = recipeMapper.recipeEntityListToListOfRecipeListDto(recipeRepository.findByIsPublicTrueOrderByLikesDesc());
        List<RecipeGlobalListDto> resultLists = new ArrayList<>();
        for (RecipeListDto list : listDtos) {
            resultLists.addFirst(RecipeGlobalListDto.builder()
                .id(list.getId())
                .name(list.getName())
                .likes(list.getLikes())
                .dislikes(list.getDislikes())
                .likedByUser(user.getLikedRecipes().stream().anyMatch(o -> o.getId().equals(list.getId())))
                .dislikedByUser(user.getDislikedRecipes().stream().anyMatch(o -> o.getId().equals(list.getId())))
                .build());
        }
        resultLists.sort(Comparator.comparingInt((RecipeGlobalListDto r) -> r.getLikes() - r.getDislikes()).reversed());
        return resultLists;
    }

    @Override
    @Transactional
    public void deleteRecipe(long id) {
        LOGGER.debug("Delete recipe with id {}", id);
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);

        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();


            ApplicationUser owner = recipe.getOwner();
            if (owner != null) {
                owner.getRecipes().remove(recipe);
                userRepository.save(owner);
            }


            for (ApplicationUser user : recipe.getLikedByUsers()) {
                user.getLikedRecipes().remove(recipe);
                userRepository.save(user);
            }
            for (ApplicationUser user : recipe.getDislikedByUsers()) {
                user.getDislikedRecipes().remove(recipe);
                userRepository.save(user);
            }


            recipeRepository.delete(recipe);


        }
    }

    @Override
    @Transactional
    public RecipeDetailDto likeRecipe(long recipeId, ApplicationUser user) throws AlreadyRatedException {

        user = userRepository.findApplicationUserByIdWithLikeInfo(user.getId());
        Optional<Recipe> optional = recipeRepository.findById(recipeId);
        if (optional.isPresent()) {
            Recipe recipe = optional.get();
            if (user.getLikedRecipes().contains(recipe)) {
                throw new AlreadyRatedException("User already liked the recipe");
            }


            // Remove dislike if it exists
            long userid = user.getId();
            if (recipe.getDislikedByUsers().stream().anyMatch(o -> o.getId().equals(userid))) {
                recipe.removeDisliker(user);
                user.removeDisLike(recipe);

                recipeRepository.save(recipe);
                userRepository.save(user);

            }

            // Add like if not already liked
            if (!recipe.getLikedByUsers().contains(user)) {
                recipe.addLiker(user);
                user.addRecipeLike(recipe);
            }
            ApplicationUser owner = recipe.getOwner();

            userRepository.saveAndFlush(user);
            recipeRepository.save(recipe);
            userRepository.saveAndFlush(owner); // Ensure the owner's state is updated


            return recipeMapper.recipeEntityToRecipeDetailDto(recipeRepository.saveAndFlush(recipe));
        } else {
            throw new NotFoundException("Could not find recipe to like");
        }
    }


    @Override
    @Transactional
    public RecipeDetailDto dislikeRecipe(long recipeId, ApplicationUser user) throws AlreadyRatedException {
        user = userRepository.findApplicationUserByIdWithLikeInfo(user.getId());
        Optional<Recipe> optional = recipeRepository.findById(recipeId);
        if (optional.isPresent()) {
            Recipe recipe = optional.get();
            if (user.getDislikedRecipes().contains(recipe)) {
                throw new AlreadyRatedException("User already liked the recipe");
            }

            long userid = user.getId();
            // Remove like if it exists
            if (recipe.getLikedByUsers().stream().anyMatch(o -> o.getId().equals(userid))) {
                recipe.removeLiker(user);
                user.removeLike(recipe);
            }

            // Add like if not already liked
            if (!recipe.getDislikedByUsers().contains(user)) {
                recipe.addDisliker(user);
                user.addRecipeDislike(recipe);
            }

            userRepository.saveAndFlush(user);


            return recipeMapper.recipeEntityToRecipeDetailDto(recipeRepository.saveAndFlush(recipe));
        } else {
            throw new NotFoundException("Could not find recipe to like");
        }
    }

    @Override
    @Transactional
    public List<RecipeListDto> searchOwnRecipe(ApplicationUser owner, String searchParams) {

        List<Recipe> recipeEntities = recipeRepository.findOwnRecipesBySearchParamOrderedByLikes(searchParams.toLowerCase(), owner);

        recipeEntities.sort(Comparator.comparingInt((Recipe r) -> r.getLikes() - r.getDislikes()).reversed());
        return recipeMapper.recipeEntityListToListOfRecipeListDto(recipeEntities);
    }

    @Override
    public List<RecipeListDto> searchLikedRecipe(ApplicationUser owner, String searchParams) {
        List<Recipe> recipeEntities = recipeRepository.findLikedRecipesBySearchParamOrderedByLikes(searchParams.toLowerCase(), owner);

        recipeEntities.sort(Comparator.comparingInt((Recipe r) -> r.getLikes() - r.getDislikes()).reversed());
        return recipeMapper.recipeEntityListToListOfRecipeListDto(recipeEntities);
    }

    @Override
    public List<RecipeGlobalListDto> searchGlobalRecipe(ApplicationUser user, String searchParams) {
        user = userRepository.findApplicationUserByIdWithLikeInfo(user.getId());
        List<Recipe> recipeEntities = recipeRepository.findPublicRecipesBySearchParamOrderedByLikes(searchParams.toLowerCase());
        List<RecipeGlobalListDto> resultLists = new ArrayList<>();
        for (Recipe recipe : recipeEntities) {
            resultLists.addFirst(RecipeGlobalListDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .likes(recipe.getLikes())
                .dislikes(recipe.getDislikes())
                .likedByUser(user.getLikedRecipes().stream().anyMatch(o -> o.getId().equals(recipe.getId())))
                .dislikedByUser(user.getDislikedRecipes().stream().anyMatch(o -> o.getId().equals(recipe.getId())))
                .build());
        }
        return resultLists;
    }

    @Override
    public List<Recipe> getLikedRecipesByUserEmail(ApplicationUser user) {
        LOGGER.trace("getRecipesByUserEmail({})", user);
        return recipeRepository.findAllByLikedByUsersContains(user);
    }
}