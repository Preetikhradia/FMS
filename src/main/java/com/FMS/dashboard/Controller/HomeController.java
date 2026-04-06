package com.FMS.dashboard.Controller;

import com.FMS.dashboard.dto.dashboard.DashboardSummaryResponse;
import com.FMS.dashboard.dto.dashboard.DashboardViewModel;
import com.FMS.dashboard.dto.record.CreateRecordRequest;
import com.FMS.dashboard.dto.record.RecordFilterRequest;
import com.FMS.dashboard.dto.user.CreateUserRequest;
import com.FMS.dashboard.model.RecordType;
import com.FMS.dashboard.model.Role;
import com.FMS.dashboard.port.in.DashboardUseCase;
import com.FMS.dashboard.port.in.RecordUseCase;
import com.FMS.dashboard.port.in.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final DashboardUseCase dashboardUseCase;
    private final RecordUseCase    recordUseCase;
    private final UserUseCase      userUseCase;
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/access-denied")
    public String accessDenied(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        addNavAttributes(model, userDetails);
        return "access-denied";
    }
    @GetMapping({"/", "/dashboard"})
    public String dashboard(
            @RequestParam(defaultValue = "6") int months,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        addNavAttributes(model, userDetails);
        model.addAttribute("months", months);

        try {
            DashboardSummaryResponse summary = dashboardUseCase.getSummary(months);
            model.addAttribute("vm", buildViewModel(summary));
        } catch (Exception e) {
            log.error("Dashboard load error", e);
            model.addAttribute("errorMsg", "Dashboard error: " + e.getMessage());
        }
        return "dashboard";
    }
    @GetMapping("/records")
    public String recordsList(
            @ModelAttribute RecordFilterRequest filter,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        addNavAttributes(model, userDetails);
        model.addAttribute("filter",    filter);
        model.addAttribute("types",     RecordType.values());
        model.addAttribute("newRecord", new CreateRecordRequest());

        try {
            model.addAttribute("page", recordUseCase.getRecords(filter));
        } catch (Exception e) {
            log.error("Records load failed", e);
            model.addAttribute("errorMsg",
                    "Failed to load records: " + e.getMessage());
            model.addAttribute("page", null);
        }
        return "records";
    }

    @PostMapping("/records/create")
    public String createRecord(
            @Valid @ModelAttribute("newRecord") CreateRecordRequest req,
            BindingResult result,
            RedirectAttributes flash) {

        if (result.hasErrors()) {
            flash.addFlashAttribute("errorMsg",
                    "Validation failed: " +
                            result.getFieldError().getDefaultMessage());
            return "redirect:/records";
        }
        try {
            recordUseCase.create(req);
            flash.addFlashAttribute("successMsg", "Record created successfully.");
        } catch (Exception e) {
            log.error("Create record failed", e);
            flash.addFlashAttribute("errorMsg",
                    "Could not create record: " + e.getMessage());
        }
        return "redirect:/records";
    }
    @PostMapping("/records/{id}/delete")
    public String deleteRecord(@PathVariable Long id, RedirectAttributes flash) {
        try {
            recordUseCase.delete(id);
            flash.addFlashAttribute("successMsg", "Record deleted.");
        } catch (Exception e) {
            log.error("Delete failed id={}", id, e);
            flash.addFlashAttribute("errorMsg",
                    "Could not delete: " + e.getMessage());
        }
        return "redirect:/records";
    }
    @GetMapping("/users")
    public String usersList(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        addNavAttributes(model, userDetails);
        try {
            model.addAttribute("users",   userUseCase.listAllUsers());
            model.addAttribute("roles",   Role.values());
            model.addAttribute("newUser", new CreateUserRequest());
        } catch (Exception e) {
            log.error("Users load failed", e);
            model.addAttribute("errorMsg",
                    "Failed to load users: " + e.getMessage());
            model.addAttribute("users",   List.of());
            model.addAttribute("roles",   Role.values());
            model.addAttribute("newUser", new CreateUserRequest());
        }
        return "users";
    }
    @PostMapping("/users/create")
    public String createUser(
            @Valid @ModelAttribute("newUser") CreateUserRequest req,
            BindingResult result,
            RedirectAttributes flash) {

        if (result.hasErrors()) {
            flash.addFlashAttribute("errorMsg",
                    "Validation failed: " +
                            result.getFieldError().getDefaultMessage());
            return "redirect:/users";
        }
        try {
            userUseCase.createUser(req);
            flash.addFlashAttribute("successMsg", "User created successfully.");
        } catch (Exception e) {
            log.error("Create user failed", e);
            flash.addFlashAttribute("errorMsg",
                    "Could not create user: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateRole(
            @PathVariable Long id,
            @RequestParam Role role,
            RedirectAttributes flash) {
        try {
            userUseCase.updateRole(id, role);
            flash.addFlashAttribute("successMsg",
                    "Role updated to " + role + ".");
        } catch (Exception e) {
            log.error("Update role failed userId={}", id, e);
            flash.addFlashAttribute("errorMsg",
                    "Could not update role: " + e.getMessage());
        }
        return "redirect:/users";
    }
    @PostMapping("/users/{id}/toggle")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes flash) {
        try {
            userUseCase.toggleStatus(id);
            flash.addFlashAttribute("successMsg", "User status updated.");
        } catch (Exception e) {
            log.error("Toggle failed userId={}", id, e);
            flash.addFlashAttribute("errorMsg",
                    "Could not toggle status: " + e.getMessage());
        }
        return "redirect:/users";
    }

    private void addNavAttributes(Model model, UserDetails userDetails) {
        model.addAttribute("username", userDetails.getUsername());
        String role = userDetails.getAuthorities()
                .iterator().next()
                .getAuthority()
                .replace("ROLE_", "");
        model.addAttribute("userRole", role);
    }

    private DashboardViewModel buildViewModel(DashboardSummaryResponse summary) {
        NumberFormat fmt = NumberFormat.getInstance();
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);

        BigDecimal inc = nullSafe(summary.getTotalIncome());
        BigDecimal exp = nullSafe(summary.getTotalExpenses());
        BigDecimal net = nullSafe(summary.getNetBalance());

        Map<String, String> catFormatted = new LinkedHashMap<>();
        if (summary.getCategoryTotals() != null) {
            summary.getCategoryTotals().forEach((category, amount) ->
                    catFormatted.put(category, fmt.format(nullSafe(amount))));
        }

        return DashboardViewModel.builder()
                .totalIncome(fmt.format(inc))
                .totalExpenses(fmt.format(exp))
                .netBalance(fmt.format(net))
                .netPositive(net.compareTo(BigDecimal.ZERO) >= 0)
                .categoryCount(catFormatted.size())
                .categoryTotals(catFormatted)
                .monthlyTrends(summary.getMonthlyTrends() != null
                        ? summary.getMonthlyTrends() : List.of())
                .recentActivity(summary.getRecentActivity() != null
                        ? summary.getRecentActivity() : List.of())
                .build();
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}