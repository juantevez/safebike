package com.safe.user.infrastructure.adapters.input.web;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "/")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {
    private static final Logger logger = LoggerFactory.getLogger(HomeView.class);
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar si el usuario está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() &&
                !auth.getName().equals("anonymousUser")) {
            // Si está autenticado, redirigir a reportes
            UI.getCurrent().navigate("/reports");
        } else {
            // Si no está autenticado, redirigir al login
            UI.getCurrent().navigate("/login");
        }
    }

    public HomeView() {
        logger.info("🏠 HomeView - Constructor iniciado");

        // Configurar la vista principal
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setSpacing(true);
        setPadding(true);

        // Título de la aplicación
        H1 title = new H1(".:SAVING MY BIKE:.");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.TextColor.PRIMARY);

        logger.info("🏠 HomeView - Título creado");

        // Contenedor para los botones
        HorizontalLayout buttonContainer = new HorizontalLayout();
        buttonContainer.setSpacing(true);
        buttonContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Botón de Login
        Button loginButton = createMenuButton(
                VaadinIcon.SIGN_IN,
                "INICIAR SESIÓN",
                "Accede a tu cuenta",
                () -> {
                    logger.info("🏠 HomeView - Navegando a login");
                    UI.getCurrent().navigate("login");
                }
        );

        // Botón de Registro
        Button registerButton = createMenuButton(
                VaadinIcon.USER_CHECK,
                "REGISTRARSE",
                "Crea una nueva cuenta",
                () -> {
                    logger.info("🏠 HomeView - Navegando a register");
                    UI.getCurrent().navigate("register");
                }
        );

        // Agregar botones al contenedor
        buttonContainer.add(loginButton, registerButton);

        // Agregar componentes a la vista
        add(title, buttonContainer);

        // Estilo adicional para responsive
        addClassName("home-view");

        logger.info("🏠 HomeView - Constructor completado exitosamente");
    }

    private Button createMenuButton(VaadinIcon iconType, String text, String description, Runnable action) {
        // Crear el icono grande
        Icon icon = iconType.create();
        icon.setSize("80px");
        icon.addClassNames(LumoUtility.TextColor.PRIMARY);

        // Crear el contenido del botón
        VerticalLayout buttonContent = new VerticalLayout();
        buttonContent.setSpacing(false);
        buttonContent.setPadding(false);
        buttonContent.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonContent.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Texto principal
        Div mainText = new Div();
        mainText.setText(text);
        mainText.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY_CONTRAST
        );

        // Texto descriptivo
        Div descriptionText = new Div();
        descriptionText.setText(description);
        descriptionText.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY
        );

        // Agregar elementos al contenido
        buttonContent.add(icon, mainText, descriptionText);

        // Crear el botón
        Button button = new Button(buttonContent);
        button.addThemeVariants(ButtonVariant.LUMO_LARGE);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Estilo del botón
        button.getStyle()
                .set("min-width", "250px")
                .set("min-height", "200px")
                .set("border", "2px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("margin", "var(--lumo-space-m)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        // Efectos hover
        button.getElement().addEventListener("mouseenter", e -> {
            button.getStyle()
                    .set("border-color", "var(--lumo-primary-color)")
                    .set("box-shadow", "0 4px 20px var(--lumo-primary-color-10pct)")
                    .set("transform", "translateY(-2px)");
        });

        button.getElement().addEventListener("mouseleave", e -> {
            button.getStyle()
                    .set("border-color", "var(--lumo-contrast-20pct)")
                    .set("box-shadow", "none")
                    .set("transform", "translateY(0)");
        });

        // Acción del botón
        button.addClickListener(e -> action.run());

        return button;
    }
}