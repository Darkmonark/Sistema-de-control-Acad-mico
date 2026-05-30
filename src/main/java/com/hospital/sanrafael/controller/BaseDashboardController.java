package com.hospital.sanrafael.controller;

import com.hospital.sanrafael.service.NotificationService;
import com.hospital.sanrafael.view.ViewFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class BaseDashboardController {
protected MainController mainController;
protected ViewFactory viewFactory;
protected final NotificationService notificationService = NotificationService.getInstance();

protected BorderPane root;
protected Label titleLabel;
protected VBox mainContent;
protected String currentSection = "profile";

    protected abstract String getSidebarColor();
    protected abstract String getSidebarLogo();
    protected abstract String getSidebarLetter();
    protected abstract String getModuleName();
    protected abstract String getModuleRole();
    protected abstract String getTitle();
    protected abstract VBox createSidebarMenuItems();

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    public void setCurrentSection(String section) {
        this.currentSection = section;
    }

    public Pane getView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f4f8;");
        root.setLeft(createSidebar());
        mainContent = new VBox();
        mainContent.getChildren().addAll(createTopBar(), createContent());
        root.setCenter(mainContent);
        return root;
    }

    protected void refreshContent() {
        if (titleLabel != null) {
            titleLabel.setText(getTitle());
        }
        if (mainContent != null && mainContent.getChildren().size() >= 2) {
            mainContent.getChildren().set(1, createContent());
        }
        if (root != null) {
            root.setLeft(createSidebar());
        }
    }

    protected abstract VBox createContent();

protected VBox createSidebar() {
VBox sidebar = new VBox(15);
sidebar.setPrefWidth(250);
sidebar.setStyle("-fx-background-color: " + getSidebarColor() + ";");
sidebar.setPadding(new Insets(25, 15, 25, 15));

Label logo = new Label(getSidebarLogo());
logo.setFont(Font.font("Arial Bold", 20));
logo.setStyle("-fx-text-fill: white;");

HBox profile = new HBox(10);
profile.setAlignment(Pos.CENTER_LEFT);
profile.setPadding(new Insets(15, 0, 20, 0));

Circle avatar = new Circle(22);
avatar.setFill(Color.WHITE);
Label letter = new Label(getSidebarLetter());
letter.setFont(Font.font("Arial Bold", 16));
letter.setStyle("-fx-text-fill: " + getSidebarColor() + ";");

VBox info = new VBox(3);
Label name = new Label(getModuleName());
name.setFont(Font.font("Arial Bold", 13));
name.setStyle("-fx-text-fill: white;");
Label role = new Label(getModuleRole());
role.setFont(Font.font("Arial", 11));
role.setStyle("-fx-text-fill: derive(" + getSidebarColor() + ", 60%);");
info.getChildren().addAll(name, role);

profile.getChildren().addAll(new StackPane(avatar, letter), info);

VBox menu = createSidebarMenuItems();

HBox notificationBox = createNotificationBadge();
menu.getChildren().add(notificationBox);

sidebar.getChildren().addAll(logo, profile, menu);
return sidebar;
}

protected HBox createTopBar() {
HBox bar = new HBox();
bar.setStyle("-fx-background-color: white; -fx-padding: 15 25;");
bar.setAlignment(Pos.CENTER_LEFT);
bar.setSpacing(15);

titleLabel = new Label(getTitle());
titleLabel.setFont(Font.font("Arial Bold", 22));
titleLabel.setStyle("-fx-text-fill: #2c3e50;");

Region sp = new Region();
HBox.setHgrow(sp, Priority.ALWAYS);

Button backBtn = logoutBtn();
backBtn.setOnAction(e -> { if (mainController != null) mainController.navigateTo("login"); });

Circle c = new Circle(17);
c.setFill(Color.valueOf(getSidebarColor()));
Label l = new Label(getSidebarLetter());
l.setFont(Font.font("Arial Bold", 13));
l.setStyle("-fx-text-fill: white;");

bar.getChildren().addAll(titleLabel, sp, backBtn, new StackPane(c, l));
return bar;
}

    protected Button logoutBtn() {
        Button btn = new Button("Cerrar Sesión");
        btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-radius: 8; -fx-padding: 8 18;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-radius: 8; -fx-padding: 8 18;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-border-radius: 8; -fx-padding: 8 18;"));
        return btn;
    }

    protected Button actionBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 13));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 22; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", 70%); -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 22; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 22; -fx-cursor: hand;"));
        return btn;
    }

    protected Pane statCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
        Label t = new Label(title);
        t.setFont(Font.font("Arial", 11));
        t.setStyle("-fx-text-fill: #999;");
        Label v = new Label(value);
        v.setFont(Font.font("Arial Bold", 26));
        v.setStyle("-fx-text-fill: " + color + ";");
        card.getChildren().addAll(t, v);
        return card;
    }

    protected Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", 12));
        l.setStyle("-fx-text-fill: #555;");
        return l;
    }

    protected TextField createField() {
        TextField f = new TextField();
        f.setPrefWidth(180);
        f.setStyle("-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-border-color: #ddd; -fx-font-size: 13px;");
        return f;
    }

protected Button sidebarBtn(String text, boolean active) {
Button btn = new Button(text);
btn.setPrefSize(220, 40);
btn.setAlignment(Pos.CENTER_LEFT);
btn.setFont(Font.font("Arial", 14));
String base = active ? "rgba(255,255,255,0.2)" : "transparent";
String fg = active ? "white" : "derive(" + getSidebarColor() + ", 60%)";
btn.setStyle("-fx-background-color: " + base + "; -fx-text-fill: " + fg + "; -fx-background-radius: 10; -fx-cursor: hand;");
return btn;
}

protected HBox createNotificationBadge() {
HBox badge = new HBox(10);
badge.setAlignment(Pos.CENTER_LEFT);
badge.setPadding(new Insets(10, 10, 10, 10));
badge.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8;");

Label notifLabel = new Label("🔔");
notifLabel.setStyle("-fx-font-size: 18px;");

Label countLabel = new Label(String.valueOf(getFilteredNotificationCount()));
countLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 2 6;");
countLabel.setMinWidth(20);
countLabel.setAlignment(Pos.CENTER);

Label textLabel = new Label("Notificaciones");
textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

badge.getChildren().addAll(notifLabel, countLabel, textLabel);

badge.setOnMouseClicked(e -> {
currentSection = "view-notifications";
refreshContent();
});
badge.setStyle(badge.getStyle() + " -fx-cursor: hand;");

return badge;
}

protected int getFilteredNotificationCount() {
var user = mainController != null ? mainController.getCurrentUser() : null;
if (user == null) return 0;
String role = user.getRole();
if ("Administrador".equals(role)) {
return notificationService.getUnreadCountForAdmin();
}
return notificationService.getUnreadCount();
}

    protected VBox createAdminNotificationsSection() {
        VBox section = new VBox(15);
        section.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
        Label title = new Label("Notificaciones");
        title.setFont(Font.font("Arial Bold", 16));
        title.setStyle("-fx-text-fill: #2c3e50;");
        var notifList = notificationService.getAllNotifications().stream()
                .filter(n -> "admin".equals(n.getPersonId()) || n.getPersonId() == null)
                .toList();
        if (notifList.isEmpty()) {
            Label empty = new Label("No hay notificaciones");
            empty.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            section.getChildren().addAll(title, empty);
            return section;
        }
        Label count = new Label("Total: " + notifList.size());
        count.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
        VBox list = new VBox(8);
        list.setPadding(new Insets(10, 0, 0, 0));
        for (var n : notifList) {
            VBox card = new VBox(4);
            card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 8;");
            Label header = new Label("[" + n.getType() + "] " + n.getMessage());
            header.setFont(Font.font("Arial Bold", 12));
            header.setStyle("-fx-text-fill: #2c3e50;");
            card.getChildren().add(header);
            if (n.getDetails() != null && !n.getDetails().isEmpty()) {
                Label detail = new Label(n.getDetails());
                detail.setFont(Font.font("Arial", 11));
                detail.setStyle("-fx-text-fill: #555;");
                card.getChildren().add(detail);
            }
            if (n.getPersonName() != null) {
                Label from = new Label("De: " + n.getPersonName());
                from.setFont(Font.font("Arial", 10));
                from.setStyle("-fx-text-fill: #2C3E8F;");
                card.getChildren().add(from);
            }
            Label date = new Label("Fecha: " + n.getDate());
            date.setFont(Font.font("Arial", 10));
            date.setStyle("-fx-text-fill: #999;");
            card.getChildren().add(date);
            list.getChildren().add(card);
        }
        section.getChildren().addAll(title, count, list);
        return section;
    }

    protected void show(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    protected static class Circle extends javafx.scene.shape.Circle {
        Circle(double r) { super(r); }
    }
}
