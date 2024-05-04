package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PantrySearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/group")
public class PantryEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PantryService pantryService;
    private final ItemMapper itemMapper;

    @Autowired
    public PantryEndpoint(PantryService pantryService, ItemMapper itemMapper) {
        this.pantryService = pantryService;
        this.itemMapper = itemMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("/{pantryId}/pantry")
    public List<ItemDetailDto> findAllInPantry(@PathVariable long pantryId) {
        LOGGER.info("GET /api/v1/group/{}/pantry", pantryId);
        return itemMapper.listOfItemsToListOfItemDetailDto(pantryService.findAllItems(pantryId));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{pantryId}/pantry/search")
    public List<ItemDetailDto> searchItemsInPantry(@PathVariable long pantryId, PantrySearchDto searchParams) {
        LOGGER.info("GET /api/v1/group/{}/pantry/search", pantryId);
        LOGGER.debug("request parameters: {}", searchParams.getDetails());
        return itemMapper.listOfItemsToListOfItemDetailDto(pantryService.findItemsByDescription(searchParams.getDetails(), pantryId));
    }
}
