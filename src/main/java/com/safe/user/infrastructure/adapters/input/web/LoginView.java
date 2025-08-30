package com.safe.user.infrastructure.adapters.input.web;

import com.safe.user.application.service.AuthService;
import com.safe.user.config.AuthenticationHelper;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

    private final AuthService authService;
    private final AuthenticationHelper authHelper;

    private TextField email = new TextField("Email");
    private PasswordField password = new PasswordField("Contraseña");
    private Button loginButton = new Button("Iniciar Sesión");

    public LoginView(AuthService authService, AuthenticationHelper authHelper) {
        this.authService = authService;
        this.authHelper = authHelper;

        initializeComponents();
        setupLayout();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Si ya está autenticado, redirigir al menú principal
        if (authHelper.isAuthenticated()) {
            logger.info("Usuario ya autenticado, redirigiendo al menú principal");
            event.rerouteTo("bike-form");
        }
    }

    private void initializeComponents() {
        // Configurar campo de email
        email.setPlaceholder("ejemplo@correo.com");
        email.setAutofocus(true);
        email.setWidth("300px");

        // Configurar campo de contraseña
        password.setPlaceholder("Contraseña");
        password.setAutocomplete(Autocomplete.CURRENT_PASSWORD);
        password.setAutocorrect(false);
        password.setAutocapitalize(Autocapitalize.NONE);
        password.setWidth("300px");

        // Configurar botón de login
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("300px");
        loginButton.addClickListener(e -> performLogin());

        // Permitir login con Enter
        password.addKeyPressListener(key -> {
            if (key.getKey().equals("Enter")) {
                performLogin();
            }
        });
    }

    private void setupLayout() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        setSpacing(true);
        setPadding(true);

        add(
                new H2("🔐 Iniciar Sesión"),
                email,
                password,
                loginButton
        );
    }

    private void performLogin() {
        String emailValue = email.getValue().trim();
        String passwordValue = password.getValue();

        // Validaciones básicas
        if (emailValue.isEmpty()) {
            showErrorNotification("❌ Por favor ingrese su email");
            email.focus();
            return;
        }

        if (passwordValue.isEmpty()) {
            showErrorNotification("❌ Por favor ingrese su contraseña");
            password.focus();
            return;
        }

        // Deshabilitar botón durante el proceso
        loginButton.setEnabled(false);
        loginButton.setText("Iniciando sesión...");

        try {
            // Realizar autenticación
            String token = authService.login(emailValue, passwordValue);

            if (token != null && !token.trim().isEmpty()) {
                logger.info("Login exitoso para usuario: {}", emailValue);

                // Con esto (manejo directo de sesión):
                getUI().ifPresent(ui -> {
                    ui.getSession().setAttribute("authToken", token);
                    ui.getSession().setAttribute("userEmail", emailValue);

                    logger.info("Token y email guardados en sesión para usuario: {}", emailValue);
                });

                // Mostrar notificación de éxito
                Notification.show("🎉 ¡Login exitoso!", 2000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Mostrar menú de opciones después del login exitoso
                showMainMenu();

            } else {
                logger.error("AuthService retornó token nulo o vacío");
                showErrorNotification("❌ Error interno del servidor");
            }

        } catch (IllegalArgumentException ex) {
            logger.warn("Error de autenticación para usuario {}: {}", emailValue, ex.getMessage());
            showErrorNotification("❌ " + ex.getMessage());

        } catch (Exception ex) {
            logger.error("Error inesperado durante login para usuario {}: {}", emailValue, ex.getMessage(), ex);
            showErrorNotification("❌ Error interno del servidor");

        } finally {
            // Rehabilitar botón
            loginButton.setEnabled(true);
            loginButton.setText("Iniciar Sesión");
        }
    }

    private void showErrorNotification(String message) {
        Notification.show(message, 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
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
        String userEmail = authHelper.getCurrentUserEmail().orElse("Usuario desconocido");
        Paragraph userInfo = new Paragraph("Usuario: " + userEmail);
        userInfo.getStyle()
                .set("text-align", "center")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-bottom", "40px");

        // Botón para cargar bicicleta - ESTILOS MEJORADOS PARA iOS
        Button bikeButton = new Button("🚴 Cargar Bicicleta");
        bikeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        bikeButton.setWidth("320px");  // Más ancho para iOS
        bikeButton.setHeight("100px"); // Más alto para evitar cortes
        bikeButton.getStyle()
                .set("font-size", "18px")
                .set("margin", "10px")
                .set("padding", "20px")  // Padding interno generoso
                .set("line-height", "1.2")  // Mejor espaciado de línea
                .set("display", "flex")  // Flexbox para mejor control
                .set("align-items", "center")  // Centrar contenido verticalmente
                .set("justify-content", "center")  // Centrar horizontalmente
                .set("white-space", "normal")  // Permitir salto de línea si es necesario
                .set("text-overflow", "visible")  // No cortar texto
                .set("overflow", "visible")  // Mostrar todo el contenido
                .set("word-wrap", "break-word")  // Partir palabras largas
                .set("box-sizing", "border-box")  // Incluir padding en el tamaño
                .set("-webkit-appearance", "none")  // Resetear estilos de Safari
                .set("border-radius", "8px");  // Bordes redondeados consistentes

        bikeButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("bike-form"));
        });

        // Botón para cargar fotos - ESTILOS MEJORADOS PARA iOS
        Button photoButton = new Button("📸 Cargar Fotos de Bicicleta");
        photoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        photoButton.setWidth("320px");  // Más ancho para iOS
        photoButton.setHeight("100px"); // Más alto para evitar cortes
        photoButton.getStyle()
                .set("font-size", "18px")
                .set("margin", "10px")
                .set("padding", "20px")  // Padding interno generoso
                .set("line-height", "1.2")  // Mejor espaciado de línea
                .set("display", "flex")  // Flexbox para mejor control
                .set("align-items", "center")  // Centrar contenido verticalmente
                .set("justify-content", "center")  // Centrar horizontalmente
                .set("white-space", "normal")  // Permitir salto de línea si es necesario
                .set("text-overflow", "visible")  // No cortar texto
                .set("overflow", "visible")  // Mostrar todo el contenido
                .set("word-wrap", "break-word")  // Partir palabras largas
                .set("box-sizing", "border-box")  // Incluir padding en el tamaño
                .set("-webkit-appearance", "none")  // Resetear estilos de Safari
                .set("border-radius", "8px");  // Bordes redondeados consistentes

        photoButton.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("photo-upload"));
        });

        // Layout para los botones - CAMBIO A VERTICAL
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth("100%");
        buttonLayout.getStyle()
                .set("gap", "20px")  // Espacio entre botones
                .set("padding", "20px 0");  // Padding arriba y abajo

        buttonLayout.add(bikeButton, photoButton);

        // Botón de logout
        Button logoutButton = new Button("🚪 Cerrar Sesión");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.addClickListener(event -> {
            authHelper.clearAuthentication();
            getUI().ifPresent(ui -> ui.navigate("login"));
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
    }
}