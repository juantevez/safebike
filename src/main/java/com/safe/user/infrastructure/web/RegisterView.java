package com.safe.user.infrastructure.web;

import com.safe.user.application.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {

    private final UserService userService;

    private TextField email = new TextField("Email");
    private TextField userName = new TextField("Username");
    private TextField firstName = new TextField("Nombre");
    private TextField lastName = new TextField("Apellido");
    private TextField password = new TextField("Contraseña");
    private Button registerButton = new Button("Registrarse");

    public RegisterView(UserService userService) {
        this.userService = userService;

        // Configurar campo de contraseña
        password.setPlaceholder("Contraseña");
        password.setAutocomplete(Autocomplete.NEW_PASSWORD);
        password.setAutocapitalize(Autocapitalize.NONE);
        password.getElement().setAttribute("autocorrect", "off");
        password.getElement().setAttribute("spellcheck", "false");

        registerButton.addClickListener(e -> registrar());

        setPadding(true);
        setSpacing(true);
        add(
                new H2("📝 Registro"),
                email,
                userName,
                firstName,
                lastName,
                password,
                registerButton
        );
    }

    private void registrar() {
        String emailValue = email.getValue().trim();
        String userNameValue = userName.getValue().trim();
        String firstNameValue = firstName.getValue().trim();
        String lastNameValue = lastName.getValue().trim();
        String passwordValue = password.getValue();

        // Validaciones simples
        if (emailValue.isEmpty() || firstNameValue.isEmpty() || lastNameValue.isEmpty() ||passwordValue.isEmpty() || userNameValue.isEmpty()) {
            Notification.show("❌ Todos los campos son obligatorios.");
            return;
        }

        try {
            userService.registrarUsuario(emailValue, passwordValue, firstNameValue, lastNameValue, userNameValue);

            Notification notification = Notification.show(
                    "✅ El usuario \"" + firstNameValue + " " + lastNameValue + "\" se registró exitosamente"
            );
            notification.setPosition(Notification.Position.MIDDLE);
            notification.setDuration(1500); // 1.5 segundos

            // Redirigir cuando la notificación termine
            notification.addDetachListener(detachEvent -> {
                UI.getCurrent().navigate("");
            });

        } catch (Exception ex) {
            Notification.show("❌ Error: " + ex.getMessage());
        }
    }
}