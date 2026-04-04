package com.FMS.dashboard.Controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class HomeController {

    // ← Inject PORT INTERFACES — never concrete service classes
    private final DashboardUseCase dashboardUseCase;
    private final RecordUseCase    recordUseCase;
    private final UserUseCase      userUseCase;

    // ── Login ──────────────────────────────────────────────────────────────
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ── Dashboard ──────────────────────────────────────────────────────────
    @GetMapping({"/", "/dashboard"})
    @Transactional(readOnly = true)
    public String dashboard(Model model,
                            @RequestParam(defaultValue = "6") int months,
                            @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("summary",  dashboardUseCase.getSummary(months));
        model.addAttribute("months",   months);
        model.addAttribute("username", user.getUsername());
        return "dashboard";
    }

    // ── Records ────────────────────────────────────────────────────────────
    @GetMapping("/records")
    @Transactional(readOnly = true)
    public String recordsList(@ModelAttribute RecordFilterRequest filter, Model model) {
        model.addAttribute("page",      recordUseCase.getRecords(filter));
        model.addAttribute("filter",    filter);
        model.addAttribute("types",     RecordType.values());
        model.addAttribute("newRecord", new CreateRecordRequest());
        return "records";
    }

    @PostMapping("/records/create")
    public String createRecord(@Valid @ModelAttribute("newRecord") CreateRecordRequest req,
                               BindingResult result,
                               RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("errorMsg", "Please fix the form errors.");
            return "redirect:/records";
        }
        recordUseCase.create(req);
        flash.addFlashAttribute("successMsg", "Record created successfully.");
        return "redirect:/records";
    }

    @PostMapping("/records/{id}/delete")
    public String deleteRecord(@PathVariable Long id, RedirectAttributes flash) {
        recordUseCase.delete(id);
        flash.addFlashAttribute("successMsg", "Record deleted.");
        return "redirect:/records";
    }

    // ── Users (ADMIN only) ─────────────────────────────────────────────────
    @GetMapping("/users")
    @Transactional(readOnly = true)
    public String usersList(Model model) {
        model.addAttribute("users",   userUseCase.listAllUsers());
        model.addAttribute("roles",   Role.values());
        model.addAttribute("newUser", new CreateUserRequest());
        return "users";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("newUser") CreateUserRequest req,
                             BindingResult result,
                             RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("errorMsg", "Please fix the form errors.");
            return "redirect:/users";
        }
        userUseCase.createUser(req);
        flash.addFlashAttribute("successMsg", "User created successfully.");
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam Role role,
                             RedirectAttributes flash) {
        userUseCase.updateRole(id, role);
        flash.addFlashAttribute("successMsg", "Role updated.");
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes flash) {
        userUseCase.toggleStatus(id);
        flash.addFlashAttribute("successMsg", "User status toggled.");
        return "redirect:/users";
    }
}