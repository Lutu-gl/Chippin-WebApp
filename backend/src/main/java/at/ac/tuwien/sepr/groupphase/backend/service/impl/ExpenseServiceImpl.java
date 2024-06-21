package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ExpenseMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Category;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ExpenseRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.BudgetService;
import at.ac.tuwien.sepr.groupphase.backend.service.ExpenseService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.ExpenseValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ExpenseRepository expenseRepository;
    private final ActivityRepository activityRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseValidator expenseValidator;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    @Override
    @Transactional
    public ExpenseDetailDto getById(Long expenseId, String requesterEmail) throws NotFoundException {
        LOGGER.trace("getById({}, {})", expenseId, requesterEmail);

        ApplicationUser user = userRepository.findByEmail(requesterEmail);
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("Expense not found"));
        if (!expense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this expense");
        }

        // if expense is not archived, then all participants of the expense should definitely be in the group
        // if not they should be deleted from the participants list
        if (expense.getArchived() == null || !expense.getArchived()) {
            expense.getParticipants().keySet().removeIf(member -> !expense.getGroup().getUsers().contains(member));
        }

        for (ApplicationUser member : expense.getGroup().getUsers()) {
            if (!expense.getParticipants().containsKey(member)) {
                expense.getParticipants().put(member, 0.0);
            }
        }

        return expenseMapper.expenseEntityToExpenseDetailDto(expense);
    }

    @Override
    @Transactional
    public ResponseEntity<byte[]> getBill(Long expenseId, String requesterEmail) throws NotFoundException {
        LOGGER.trace("getBill({}, {})", expenseId, requesterEmail);

        ApplicationUser user = userRepository.findByEmail(requesterEmail);
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        if (!expense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this bill");
        }
        if (expense.getBillPath() == null) {
            throw new NotFoundException("No bill found for this expense");
        }
        Path path = Paths.get(System.getProperty("user.dir") + expense.getBillPath());
        HttpHeaders headers = getHttpHeaders(expense);

        try {
            return ResponseEntity.ok().headers(headers).body(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new NotFoundException("Error while reading the bill");
        }

    }

    private static HttpHeaders getHttpHeaders(Expense expense) {
        String extension = expense.getBillPath().substring(expense.getBillPath().lastIndexOf("."));
        String contentType = switch (extension) {
            case ".gif" -> "image/gif";
            case ".png" -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            default -> "application/octet-stream";
        };

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill" + extension);
        return headers;
    }

    private String generateRandomFileName() {
        LOGGER.trace("generateRandomFileName()");
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(40);
        for (int i = 0; i < 40; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

    private String saveBill(ExpenseCreateDto expenseCreateDto) throws ValidationException {
        String fileExtension = Objects.requireNonNull(expenseCreateDto.getBill().getOriginalFilename()).substring(expenseCreateDto.getBill().getOriginalFilename().lastIndexOf("."));
        String fileName = "/uploads/" + generateRandomFileName() + fileExtension;
        Path path = Paths.get(System.getProperty("user.dir") + fileName);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, expenseCreateDto.getBill().getBytes());
            return fileName;
        } catch (IOException e) {
            throw new ValidationException("Error while saving the bill", List.of(e.getMessage()));
        }
    }

    @Override
    @Transactional
    public ExpenseCreateDto createExpense(ExpenseCreateDto expenseCreateDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.trace("createExpense({}, {})", expenseCreateDto, creatorEmail);
        expenseValidator.validateForCreation(expenseCreateDto);
        Expense expense = expenseMapper.expenseCreateDtoToExpenseEntity(expenseCreateDto);
        ApplicationUser user = userRepository.findByEmail(creatorEmail);
        if (!expense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to create an expense in this group");
        }
        expense.setDate(LocalDateTime.now());
        expense.setDeleted(false);
        expense.setArchived(false);
        if (expense.getCategory() == null) {
            expense.setCategory(Category.Other);
        }
        if (expenseCreateDto.getBill() != null) {
            String fileName = saveBill(expenseCreateDto);
            expense.setBillPath(fileName);
        }

        budgetService.addUsedAmount(expenseCreateDto.getGroupId(), expense.getAmount(), expense.getCategory(), expense.getDate());

        Expense expenseSaved = expenseRepository.save(expense);
        Activity activityForExpense = Activity.builder()
            .category(ActivityCategory.EXPENSE)
            .expense(expenseSaved)
            .timestamp(LocalDateTime.now())
            .group(expenseSaved.getGroup())
            .user(user)
            .build();

        activityRepository.save(activityForExpense);

        return expenseMapper.expenseEntityToExpenseCreateDto(expenseSaved);
    }

    @Override
    @Transactional
    public ExpenseCreateDto updateExpense(Long expenseId, ExpenseCreateDto expenseCreateDto, String updaterEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.trace("updateExpense({}, {})", expenseId, updaterEmail);
        Expense existingExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        expenseCreateDto.setGroupId(existingExpense.getGroup().getId());
        expenseValidator.validateForUpdate(expenseCreateDto, existingExpense);

        Expense expense = expenseMapper.expenseCreateDtoToExpenseEntity(expenseCreateDto);
        ApplicationUser user = userRepository.findByEmail(updaterEmail);
        if (!existingExpense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to update this expense");
        }

        expense.setId(expenseId);
        expense.setDate(existingExpense.getDate());
        if (expense.getCategory() == null) {
            expense.setCategory(Category.Other);
        }

        if ((existingExpense.getAmount() != expense.getAmount()) || existingExpense.getCategory() != expense.getCategory()) {
            budgetService.removeUsedAmount(existingExpense.getGroup().getId(), existingExpense.getAmount(), existingExpense.getCategory(), existingExpense.getDate());
            budgetService.addUsedAmount(expenseCreateDto.getGroupId(), expense.getAmount(), expense.getCategory(), expense.getDate());
        }
        expense.setDeleted(existingExpense.isDeleted());
        expense.setArchived(existingExpense.getArchived());

        // delete the old bill
        if (existingExpense.getBillPath() != null) {
            Path oldPath = Paths.get(System.getProperty("user.dir") + existingExpense.getBillPath());
            try {
                Files.deleteIfExists(oldPath);
            } catch (IOException e) {
                throw new ValidationException("Error while deleting the old bill", List.of(e.getMessage()));
            }
        }

        // save the new bill
        if (expenseCreateDto.getBill() != null) {
            String fileName = saveBill(expenseCreateDto);
            expense.setBillPath(fileName);
        }

        Expense expenseSaved = expenseRepository.save(expense);

        Activity activityForExpenseUpdate = Activity.builder()
            .category(ActivityCategory.EXPENSE_UPDATE)
            .expense(expenseSaved)
            .timestamp(LocalDateTime.now())
            .group(expenseSaved.getGroup())
            .user(user)
            .build();

        activityRepository.save(activityForExpenseUpdate);

        return expenseMapper.expenseEntityToExpenseCreateDto(expenseSaved);
    }

    @Override
    @Transactional
    public void deleteExpense(Long expenseId, String deleterEmail) throws NotFoundException, ConflictException {
        LOGGER.trace("deleteExpense({}, {})", expenseId, deleterEmail);

        Expense existingExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        ApplicationUser user = userRepository.findByEmail(deleterEmail);

        if (!existingExpense.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to delete this expense");
        }

        if (existingExpense.isDeleted()) {
            throw new ConflictException("Invalid delete operation", List.of("Expense is already marked as deleted"));
        }

        expenseRepository.markExpenseAsDeleted(existingExpense);

        if (existingExpense.getDate() != null) {
            budgetService.removeUsedAmount(existingExpense.getGroup().getId(), existingExpense.getAmount(), existingExpense.getCategory(), existingExpense.getDate());
        }

        Activity activityForExpenseDelete = Activity.builder()
            .category(ActivityCategory.EXPENSE_DELETE)
            .timestamp(LocalDateTime.now())
            .expense(existingExpense)
            .group(existingExpense.getGroup())
            .user(user)
            .build();

        activityRepository.save(activityForExpenseDelete);
    }

    @Override
    @Transactional
    public ExpenseCreateDto recoverExpense(Long expenseId, String recoverEmail) throws NotFoundException, ConflictException {
        LOGGER.trace("recoverExpense({}, {})", expenseId, recoverEmail);

        Expense existingExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new NotFoundException("No expense found with this id"));
        GroupEntity existingGroup = existingExpense.getGroup();
        ApplicationUser user = userRepository.findByEmail(recoverEmail);

        if (!existingGroup.getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to recover this expense");
        }

        if (!existingExpense.isDeleted()) {
            throw new ConflictException("Invalid recover operation", List.of("Expense is not marked as deleted"));
        }

        expenseRepository.markExpenseAsRecovered(existingExpense);


        if (existingExpense.getDate() != null) {
            budgetService.addUsedAmount(existingExpense.getGroup().getId(), existingExpense.getAmount(), existingExpense.getCategory(), existingExpense.getDate());
        }
        existingExpense.setDeleted(false);

        Activity activityForExpenseRecover = Activity.builder()
            .category(ActivityCategory.EXPENSE_RECOVER)
            .timestamp(LocalDateTime.now())
            .expense(existingExpense)
            .group(existingGroup)
            .user(user)
            .build();

        activityRepository.save(activityForExpenseRecover);

        return expenseMapper.expenseEntityToExpenseCreateDto(existingExpense);
    }
}
