package com.safe.user.infrastructure.adapters.input.web;

import com.safe.location.domain.model.entity.LocalidadEntity;
import com.safe.location.domain.model.entity.MunicipioEntity;
import com.safe.location.domain.model.entity.ProvinciaEntity;
import com.safe.user.application.service.GeografiaService;
import com.safe.user.application.service.UserServiceImpl;
import com.safe.user.domain.model.entity.User;
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

import java.util.List;

@Route(value = "/register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout {

    // ✅ SOLO DECLARAR LOS CAMPOS - Spring inyectará en el constructor
    private final UserServiceImpl userService;
    private final GeografiaService geografiaService;

    private TextField email = new TextField("Email");
    private TextField userName = new TextField("Username");
    private TextField firstName = new TextField("Nombre");
    private TextField lastName = new TextField("Apellido");
    private ComboBox<ProvinciaEntity> provinciaCombo = new ComboBox<>("Provincia");
    private ComboBox<MunicipioEntity> municipioCombo = new ComboBox<>("Municipio");
    private ComboBox<LocalidadEntity> localidadCombo = new ComboBox<>("Localidad");
    private PasswordField password = new PasswordField("Contraseña");
    private Button registerButton = new Button("Registrarse");

    private Binder<User> binder = new BeanValidationBinder<>(User.class);
    private User currentUser;

    // ✅ CONSTRUCTOR LIMPIO - Spring inyecta automáticamente
    public RegisterView(UserServiceImpl userService, GeografiaService geografiaService) {
        this.userService = userService;
        this.geografiaService = geografiaService;

        // Configurar todo después de que las dependencias estén disponibles
        configureFields();
        configureComboBoxes();
        configureBinder();
        createLayout();
    }

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
        try {
            // ✅ AÑADIR VERIFICACIÓN DE NULL Y MANEJO DE ERRORES
            if (geografiaService == null) {
                Notification.show("❌ Error: Servicio de geografía no disponible");
                return;
            }

            // Configurar ComboBox de Provincia
            List<ProvinciaEntity> provincias = geografiaService.getAllProvincias();
            if (provincias != null && !((List<?>) provincias).isEmpty()) {
                provinciaCombo.setItems(provincias);
                provinciaCombo.setItemLabelGenerator(ProvinciaEntity::getNombre);
                provinciaCombo.setPlaceholder("Selecciona una provincia");
                provinciaCombo.setClearButtonVisible(true);
            } else {
                provinciaCombo.setPlaceholder("No hay provincias disponibles");
                provinciaCombo.setEnabled(false);
            }

            // Configurar ComboBox de Municipio
            municipioCombo.setItemLabelGenerator(MunicipioEntity::getNombre);
            municipioCombo.setPlaceholder("Selecciona un municipio");
            municipioCombo.setClearButtonVisible(true);
            municipioCombo.setEnabled(false);

            // Configurar ComboBox de Localidad
            localidadCombo.setItemLabelGenerator(LocalidadEntity::getNombre);
            localidadCombo.setPlaceholder("Selecciona una localidad");
            localidadCombo.setClearButtonVisible(true);
            localidadCombo.setEnabled(false);

            // Configurar listeners para cascada
            provinciaCombo.addValueChangeListener(event -> {
                try {
                    ProvinciaEntity provincia = event.getValue();
                    municipioCombo.clear();
                    localidadCombo.clear();

                    if (provincia != null) {
                        List<MunicipioEntity> municipios = geografiaService.getMunicipiosByProvincia(provincia.getId());
                        if (municipios != null && !municipios.isEmpty()) {
                            municipioCombo.setItems(municipios);
                            municipioCombo.setEnabled(true);
                        } else {
                            municipioCombo.setPlaceholder("No hay municipios disponibles");
                            municipioCombo.setEnabled(false);
                        }
                    } else {
                        municipioCombo.setEnabled(false);
                    }
                    localidadCombo.setEnabled(false);
                } catch (Exception ex) {
                    Notification.show("❌ Error al cargar municipios: " + ex.getMessage());
                }
            });

            municipioCombo.addValueChangeListener(event -> {
                try {
                    MunicipioEntity municipio = event.getValue();
                    localidadCombo.clear();

                    if (municipio != null) {
                        List<LocalidadEntity> localidades = geografiaService.getLocalidadesByMunicipio(municipio.getId());
                        if (localidades != null && !localidades.isEmpty()) {
                            localidadCombo.setItems(localidades);
                            localidadCombo.setEnabled(true);
                        } else {
                            localidadCombo.setPlaceholder("No hay localidades disponibles");
                            localidadCombo.setEnabled(false);
                        }
                    } else {
                        localidadCombo.setEnabled(false);
                    }
                } catch (Exception ex) {
                    Notification.show("❌ Error al cargar localidades: " + ex.getMessage());
                }
            });

        } catch (Exception ex) {
            Notification.show("❌ Error al configurar geografía: " + ex.getMessage());
            // Deshabilitar campos si hay error
            provinciaCombo.setEnabled(false);
            municipioCombo.setEnabled(false);
            localidadCombo.setEnabled(false);
        }
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

        // ✅ BINDING MEJORADO PARA GEOGRAFÍA
        // Binding para Provincia
        binder.forField(provinciaCombo)
                .bind(
                        user -> {
                            if (user.getProvinciaId() != null && geografiaService != null) {
                                return geografiaService.getProvinciaById(user.getProvinciaId()).orElse(null);
                            }
                            return null;
                        },
                        (user, provincia) -> {
                            if (provincia != null) {
                                user.setProvinciaId(provincia.getId());
                            } else {
                                user.setProvinciaId(null);
                            }
                        }
                );

        // Binding para Municipio
        binder.forField(municipioCombo)
                .bind(
                        user -> {
                            if (user.getMunicipioId() != null && geografiaService != null) {
                                return geografiaService.getMunicipioById(user.getMunicipioId()).orElse(null);
                            }
                            return null;
                        },
                        (user, municipio) -> {
                            if (municipio != null) {
                                user.setMunicipioId(municipio.getId());
                            } else {
                                user.setMunicipioId(null);
                            }
                        }
                );

        // Binding para Localidad
        binder.forField(localidadCombo)
                .bind(
                        user -> {
                            if (user.getLocalidadId() != null && geografiaService != null) {
                                return geografiaService.getLocalidadById(user.getLocalidadId()).orElse(null);
                            }
                            return null;
                        },
                        (user, localidad) -> {
                            if (localidad != null) {
                                user.setLocalidadId(localidad.getId());
                            } else {
                                user.setLocalidadId(null);
                            }
                        }
                );
    }

    private void createLayout() {
        // Limpiar contenido previo
        removeAll();

        // Crear FormLayout para organizar los campos
        FormLayout formLayout = new FormLayout();

        // Agregar campos básicos
        formLayout.add(email, userName);
        formLayout.add(firstName, lastName);
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

        // Agregar todo al layout principal
        setPadding(true);
        setSpacing(true);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        add(
                new H2("📝 Registro"),
                formLayout,
                password,
                registerButton
        );
    }

    private void registrar() {
        // Crear nuevo usuario
        User newUser = new User();

        // Intentar hacer bind de todos los campos
        if (binder.writeBeanIfValid(newUser)) {
            try {
                // Verificar que el servicio esté disponible
                if (userService == null) {
                    Notification.show("❌ Error: Servicio de usuario no disponible");
                    return;
                }

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

                notification.addDetachListener(detachEvent -> {
                    UI.getCurrent().navigate("");
                });

            } catch (Exception ex) {
                Notification.show("❌ Error: " + ex.getMessage());
            }
        } else {
            Notification.show("❌ Por favor, corrige los errores en el formulario");
        }
    }

    public void initForNewUser() {
        User newUser = new User();
        binder.setBean(newUser);
    }

    public void editUser(User user) {
        currentUser = user;
        binder.setBean(user);

        // ✅ CARGAR CASCADA CON VERIFICACIONES DE NULL
        if (user.getProvinciaId() != null && geografiaService != null) {
            geografiaService.getProvinciaById(user.getProvinciaId())
                    .ifPresent(provincia -> {
                        provinciaCombo.setValue(provincia);

                        if (user.getMunicipioId() != null) {
                            municipioCombo.setItems(geografiaService.getMunicipiosByProvincia(provincia.getId()));
                            municipioCombo.setEnabled(true);

                            geografiaService.getMunicipioById(user.getMunicipioId())
                                    .ifPresent(municipio -> {
                                        municipioCombo.setValue(municipio);

                                        if (user.getLocalidadId() != null) {
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