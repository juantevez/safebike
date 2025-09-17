package com.safe.user.infrastructure.adapters.input.web;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends VerticalLayout implements RouterLayout, HasStyle {

    public MainLayout() {
        // Crear la cabecera
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("100%");
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.setHeight("60px");
        header.getStyle().set("padding", "0 20px");

        // Título con ícono de reportes
        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(Alignment.CENTER);

        Icon reportsIcon = VaadinIcon.CHART.create();
        reportsIcon.getStyle().set("margin-right", "10px");
        H1 title = new H1("SAVING MY BIKE");

        titleSection.add(reportsIcon, title);

        // Sección derecha con botón de logout
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setAlignItems(Alignment.CENTER);

        // Botón de modo oscuro
        rightSection.add(createDarkModeButton());

        // Botón de logout
        Button logoutButton = new Button("Cerrar Sesión", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(e -> {
            VaadinSession.getCurrent().getSession().invalidate();
            getUI().ifPresent(ui -> ui.navigate("/login"));
        });
        logoutButton.getStyle().set("margin-left", "10px");

        rightSection.add(logoutButton);

        header.add(titleSection, rightSection);

        // Añadir el header al layout principal
        add(header);

        // Estilos del layout
        getStyle().set("min-height", "100vh");
        setPadding(true);
        setSpacing(true);
    }

    /**
     * Crea el botón para alternar entre modo claro y oscuro
     */
    private Button createDarkModeButton() {
        Icon icon = VaadinIcon.MOON.create();
        Button button = new Button(icon, event -> {
            // Alternar clase en el body
            getUI().ifPresent(ui -> ui.getPage().executeJs("document.body.classList.toggle('dark-mode')"));

            // Obtener el estado actual del modo oscuro (desde la clase del body)
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("return document.body.classList.contains('dark-mode')")
                        .then(Boolean.class, isDark -> {
                            // Guardar en localStorage
                            ui.getPage().executeJs("localStorage.setItem('dark-mode', $0)", isDark);

                            // Cambiar icono según el nuevo estado
                            icon.getElement().setAttribute("icon", isDark ? "vaadin:sun" : "vaadin:moon");
                        });
            });
        });
        button.setThemeName("icon");

        // Restaurar estado al cargar
        getUI().ifPresent(ui -> {
            ui.getPage().executeJs("return localStorage.getItem('dark-mode') === 'true'")
                    .then(Boolean.class, isDark -> {
                        String iconName = isDark ? "vaadin:sun" : "vaadin:moon";
                        icon.getElement().setAttribute("icon", iconName);
                        String cmd = isDark ? "document.body.classList.add('dark-mode')"
                                : "document.body.classList.remove('dark-mode')";
                        ui.getPage().executeJs(cmd);
                    });
        });

        return button;
    }
}