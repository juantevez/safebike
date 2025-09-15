package com.safe.user.infrastructure.adapters.input.web;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.user.application.service.GeografiaService;
import com.safe.user.application.service.UserServiceImpl;
import com.safe.user.domain.model.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "/register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {

    @Autowired
    private final UserServiceImpl userService;

    @Autowired
    private GeografiaService geografiaService;

    private TextField email = new TextField("Email");
    private TextField userName = new TextField("Username");
    private TextField firstName = new TextField("Nombre");
    private TextField lastName = new TextField("Apellido");
    private ComboBox<ProvinciaEntity> provinciaCombo = new ComboBox<>("Provincia");
    private ComboBox<MunicipioEntity> municipioCombo = new ComboBox<>("Municipio");
    private ComboBox<LocalidadEntity> localidadCombo = new ComboBox<>("Localidad");
    private PasswordField password = new PasswordField("Contraseña"); // ✅ Usar PasswordField
    private Button registerButton = new Button("Registrarse");

    private Binder<User> binder = new BeanValidationBinder<>(User.class);
    private User currentUser;

    public RegisterView(UserServiceImpl userService) {
        this.userService = userService;

        // ✅ CONFIGURAR TODO EN EL CONSTRUCTOR
        configureFields();
        configureComboBoxes(); // ✅ LLAMAR ESTE MÉTODO
        configureBinder();     // ✅ LLAMAR ESTE MÉTODO
        createLayout();        // ✅ LLAMAR ESTE MÉTODO
    }

    // ✅ MÉTODO PARA CONFIGURAR CAMPOS BÁSICOS
    private void configureFields() {
        // Configurar campo de contraseña
        password.setPlaceholder("Contraseña");
        password.setAutocomplete(Autocomplete.NEW_PASSWORD);
        password.setAutocapitalize(Autocapitalize.NONE);
        password.getElement().setAttribute("autocorrect", "off");
        password.getElement().setAttribute("spellcheck", "false");

        // Configurar botón
        registerButton.addClickListener(e -> registrar());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private void configureComboBoxes() {
        // Configurar ComboBox de Provincia
        provinciaCombo.setItems(geografiaService.getAllProvincias());
        provinciaCombo.setItemLabelGenerator(ProvinciaEntity::getNombre);
        provinciaCombo.setPlaceholder("Selecciona una provincia");
        provinciaCombo.setClearButtonVisible(true);

        // Configurar ComboBox de Municipio
        municipioCombo.setItemLabelGenerator(MunicipioEntity::getNombre);
        municipioCombo.setPlaceholder("Selecciona un municipio");
        municipioCombo.setClearButtonVisible(true);
        municipioCombo.setEnabled(false); // Deshabilitado hasta seleccionar provincia

        // Configurar ComboBox de Localidad
        localidadCombo.setItemLabelGenerator(LocalidadEntity::getNombre);
        localidadCombo.setPlaceholder("Selecciona una localidad");
        localidadCombo.setClearButtonVisible(true);
        localidadCombo.setEnabled(false); // Deshabilitado hasta seleccionar municipio

        // Configurar listeners para cascada
        provinciaCombo.addValueChangeListener(event -> {
            ProvinciaEntity provincia = event.getValue();
            municipioCombo.clear();
            localidadCombo.clear();

            if (provincia != null) {
                // ✅ CORREGIDO: usar getId() en lugar de getProvinciaId()
                municipioCombo.setItems(geografiaService.getMunicipiosByProvincia(provincia.getId()));
                municipioCombo.setEnabled(true);
            } else {
                municipioCombo.setEnabled(false);
            }
            localidadCombo.setEnabled(false);
        });

        municipioCombo.addValueChangeListener(event -> {
            MunicipioEntity municipio = event.getValue();
            localidadCombo.clear();

            if (municipio != null) {
                // ✅ CORREGIDO: usar getId() en lugar de getMunicipioId()
                localidadCombo.setItems(geografiaService.getLocalidadesByMunicipio(municipio.getId()));
                localidadCombo.setEnabled(true);
            } else {
                localidadCombo.setEnabled(false);
            }
        });
    }

    private void configureBinder() {
        // Binding básicos
        binder.forField(email)
                .asRequired("Email es obligatorio")
                .withValidator(email -> email.contains("@"), "Email debe contener @")
                .bind(User::getEmail, User::setEmail);

        binder.forField(userName)
                .asRequired("Username es obligatorio")
                .bind(User::getUsername, User::setUsername);

        binder.forField(firstName)
                .asRequired("Nombre es obligatorio")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lastName)
                .asRequired("Apellido es obligatorio")
                .bind(User::getLastName, User::setLastName);

        // Binding para Provincia
        binder.forField(provinciaCombo)
                .bind(
                        user -> geografiaService.getProvinciaById(user.getProvinciaId()).orElse(null),
                        (user, provincia) -> {
                            if (provincia != null) {
                                // ✅ CORREGIDO: usar getId()
                                user.setProvinciaId(provincia.getId());
                            } else {
                                user.setProvinciaId(null);
                            }
                        }
                );

        // Binding para Municipio
        binder.forField(municipioCombo)
                .bind(
                        user -> geografiaService.getMunicipioById(user.getMunicipioId()).orElse(null),
                        (user, municipio) -> {
                            if (municipio != null) {
                                // ✅ CORREGIDO: usar getId()
                                user.setMunicipioId(municipio.getId());
                            } else {
                                user.setMunicipioId(null);
                            }
                        }
                );

        // Binding para Localidad
        binder.forField(localidadCombo)
                .bind(
                        user -> geografiaService.getLocalidadById(user.getLocalidadId()).orElse(null),
                        (user, localidad) -> {
                            if (localidad != null) {
                                // ✅ CORREGIDO: usar getId()
                                user.setLocalidadId(localidad.getId());
                            } else {
                                user.setLocalidadId(null);
                            }
                        }
                );
    }

    // ✅ MÉTODO CREATELAYOUT CORREGIDO
    private void createLayout() {
        // Limpiar contenido previo
        removeAll();

        // Crear FormLayout para organizar los campos
        FormLayout formLayout = new FormLayout();

        // Agregar campos básicos
        formLayout.add(email, userName);
        formLayout.add(firstName, lastName);

        // ✅ AGREGAR LOS COMBOBOX AL LAYOUT
        formLayout.add(provinciaCombo, municipioCombo, localidadCombo);

        // Configurar responsive columns
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // Configurar spans
        formLayout.setColspan(provinciaCombo, 2);
        formLayout.setColspan(municipioCombo, 1);
        formLayout.setColspan(localidadCombo, 1);

        // ✅ AGREGAR TODO AL LAYOUT PRINCIPAL
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(
                new H2("📝 Registro"),
                formLayout,        // ✅ AGREGAR EL FORMLAYOUT
                password,          // Contraseña fuera del FormLayout para darle más ancho
                registerButton
        );
    }

    // ✅ MÉTODO REGISTRAR MEJORADO
    private void registrar() {
        // Crear nuevo usuario
        User newUser = new User();

        // Intentar hacer bind de todos los campos
        if (binder.writeBeanIfValid(newUser)) {
            try {
                // ✅ INCLUIR DATOS GEOGRÁFICOS EN EL REGISTRO
                userService.registrarUsuarioConDatosGeograficos(
                        newUser.getEmail(),
                        password.getValue(),
                        newUser.getFirstName(),
                        newUser.getLastName(),
                        newUser.getUsername(),
                        newUser.getProvinciaId(),
                        newUser.getMunicipioId(),
                        newUser.getLocalidadId()
                );

                Notification notification = Notification.show(
                        "✅ El usuario \"" + newUser.getFirstName() + " " + newUser.getLastName() + "\" se registró exitosamente"
                );
                notification.setPosition(Notification.Position.MIDDLE);
                notification.setDuration(1500);

                // Redirigir cuando la notificación termine
                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate("");
                });

            } catch (Exception ex) {
                Notification.show("❌ Error: " + ex.getMessage());
            }
        } else {
            // Mostrar errores de validación
            Notification.show("❌ Por favor, corrige los errores en el formulario");
        }
    }

    // ✅ MÉTODO PARA INICIALIZAR CON USUARIO VACÍO (PARA REGISTRO)
    public void initForNewUser() {
        User newUser = new User();
        binder.setBean(newUser);
    }

    // Método editUser mantenerlo para compatibilidad
    public void editUser(User user) {
        currentUser = user;
        binder.setBean(user);

        // Cargar cascada si el usuario tiene datos geográficos
        if (user.getProvinciaId() != null) {
            geografiaService.getProvinciaById(user.getProvinciaId())
                    .ifPresent(provincia -> {
                        provinciaCombo.setValue(provincia);

                        if (user.getMunicipioId() != null) {
                            // ✅ CORREGIDO: usar getId()
                            municipioCombo.setItems(geografiaService.getMunicipiosByProvincia(provincia.getId()));
                            municipioCombo.setEnabled(true);

                            geografiaService.getMunicipioById(user.getMunicipioId())
                                    .ifPresent(municipio -> {
                                        municipioCombo.setValue(municipio);

                                        if (user.getLocalidadId() != null) {
                                            // ✅ CORREGIDO: usar getId()
                                            localidadCombo.setItems(geografiaService.getLocalidadesByMunicipio(municipio.getId()));
                                            localidadCombo.setEnabled(true);

                                            geografiaService.getLocalidadById(user.getLocalidadId())
                                                    .ifPresent(localidadCombo::setValue);
                                        }
                                    });
                        }
                    });
        }
    }
}