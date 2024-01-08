package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.services.PersonService;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.util.PersonValidator;

import javax.validation.Valid;
import java.util.Set;

@Controller
public class AdminController {

    private final String redirect = "redirect:/admin";
    private final PersonService personService;
    private final PersonValidator personValidator;
    private final RoleService roleService;

    @Autowired
    public AdminController(PersonService personService, PersonValidator personValidator, RoleService roleService) {
        this.personService = personService;
        this.personValidator = personValidator;
        this.roleService = roleService;
    }


    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("person", personService.getAllPeople());
        return "/hello/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("editUser", personService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "/admin/edit";
    }

    @PatchMapping("/edit/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("editUser") @Valid Person updatePerson,
                         @RequestParam(value = "roles", required = false) Set<Integer> roleIds,
                         BindingResult bindingResult) {


        if (bindingResult.hasErrors())
            return "/admin/edit";

        personService.editUserAndHisRoles(id, updatePerson, roleIds);
        return redirect;
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        personService.deleteUserById(id);
        return redirect;
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person, Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        return "/auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person,
                                      @RequestParam(value = "roles", required = false) Set<Integer> roleIds,
                                      BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration";

        personService.addUser(person, roleIds);
        return redirect;
    }
}
