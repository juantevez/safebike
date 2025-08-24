package com.safe.user.infrastructure.web;

import com.safe.user.application.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout {

    private final AuthService authService;

    private TextField email = new TextField("Email");
    private PasswordField password = new PasswordField("Contraseña");

    private Button loginButton = new Button("Iniciar Sesión");

    public LoginView(AuthService authService) {
        this.authService = authService;
        password.setPlaceholder("Contraseña");
        password.setAutocomplete(Autocomplete.NEW_PASSWORD);
        password.setAutocorrect(false);
        password.setAutocapitalize(Autocapitalize.NONE);


        loginButton.addClickListener(e -> login());

        add(
                new H2("🔐 Iniciar Sesión"),
                email,
                password,
                loginButton
        );
    }

    private void login() {
        try {
            String token = authService.login(email.getValue(), password.getValue());
            if (token != null) {
                System.out.println("✅ Login exitoso, token generado para: " + email.getValue());

                getUI().ifPresent(ui -> {
                    ui.getSession().setAttribute("authToken", token);
                    ui.getSession().setAttribute("userEmail", email.getValue());

                    // DEBUGGING: Verificar que se guardó correctamente
                    String savedToken = (String) ui.getSession().getAttribute("authToken");
                    String savedEmail = (String) ui.getSession().getAttribute("userEmail");

                    System.out.println("Token guardado en sesión: " + (savedToken != null ? "presente" : "AUSENTE"));
                    System.out.println("Email guardado en sesión: " + savedEmail);

                    ui.navigate("bike-form");
                });
            } else {
                System.out.println("❌ AuthService retornó token nulo");
                Notification.show("❌ Error: Token no generado");
            }
        } catch (Exception ex) {
            System.out.println("❌ Error durante login: " + ex.getMessage());
            Notification.show("❌ " + ex.getMessage());
        }
    }
}