package com.safe.user.infrastructure.adapters.input.web;

import com.safe.user.application.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

                    // Mostrar menú de opciones después del login exitoso
                    showMainMenu();
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

    private void showMainMenu() {
        // Limpiar el contenido actual
        removeAll();

        // Título de bienvenida
        H2 welcomeTitle = new H2("🎉 ¡Bienvenido!");
        welcomeTitle.getStyle()
                .set("text-align", "center")
                .set("color", "var(--lumo-primary-text-color)")
                .set("margin-bottom", "30px");

        // Subtítulo con el email del usuario
        String userEmail = (String) getUI().get().getSession().getAttribute("userEmail");
        Paragraph userInfo = new Paragraph("Usuario: " + userEmail);
        userInfo.getStyle()
                .set("text-align", "center")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-bottom", "40px");

        // Botón para cargar bicicleta
        Button bikeButton = new Button("🚴 Cargar Bicicleta");
        bikeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        bikeButton.setWidth("300px");
        bikeButton.setHeight("80px");
        bikeButton.getStyle()
                .set("font-size", "18px")
                .set("margin", "10px");

        bikeButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("bike-form"));
        });

        // Botón para cargar fotos
        Button photoButton = new Button("📸 Cargar Fotos de Bicicleta");
        photoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        photoButton.setWidth("300px");
        photoButton.setHeight("80px");
        photoButton.getStyle()
                .set("font-size", "18px")
                .set("margin", "10px");

        photoButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("photo-upload"));
        });

        // Layout para los botones
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.add(bikeButton, photoButton);

        // Botón de logout (opcional)
        Button logoutButton = new Button("🚪 Cerrar Sesión");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClickListener(event -> {
            getUI().ifPresent(ui -> {
                ui.getSession().setAttribute("authToken", null);
                ui.getSession().setAttribute("userEmail", null);
                ui.navigate("login"); // Asume que tu ruta de login es "login"
            });
        });

        // Layout principal del menú
        VerticalLayout mainMenuLayout = new VerticalLayout();
        mainMenuLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        mainMenuLayout.setSizeFull();
        mainMenuLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainMenuLayout.setSpacing(true);
        mainMenuLayout.setPadding(true);

        mainMenuLayout.add(
                welcomeTitle,
                userInfo,
                buttonLayout,
                logoutButton
        );

        // Agregar el layout a la vista actual
        add(mainMenuLayout);

        // Mostrar notificación de bienvenida
        Notification.show("🎉 ¡Login exitoso! Selecciona una opción", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}