package com.foodhub.controllers;

import com.foodhub.Global;
import com.foodhub.models.Order;
import com.foodhub.models.UserData;
import com.foodhub.utils.Navigator;
import com.foodhub.utils.Utils;
import com.foodhub.views.LabeledSeparator;
import com.foodhub.views.NodeSwitcher;
import com.foodhub.views.OrderTreeItemView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProfileController implements Initializable {

    @FXML private LoginController loginController;
    @FXML private SignUpController signUpController;
    @FXML private NodeSwitcher switcher;
    @FXML private Label fullName, email, phone, userName;
    @FXML private TreeView<Order> orders;
    @FXML private TreeItem<Order> ordersRoot;
    @FXML private LabeledSeparator ordersSeparator;
    private Navigator navigator;
    private UserData user;

    private static final ProfileController profile = new ProfileController();

    public static final ProfileController instance() {

        return profile;

    }
    
    private ProfileController() {
        
        
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.navigator = (Navigator) resources.getObject("navigator");
        navigator.addNavi(() -> {

            switcher.setCurrentNodeIndex(0);
            return true;

        }, null);
        switcher.setCurrentNodeIndex(0);
        orders.setCellFactory(treeView -> new TreeCell<>() {

            @Override
            protected void updateItem(Order item, boolean empty) {

                super.updateItem(item, empty);

                setText(null);

                if(empty) setGraphic(null);
                else {

                    OrderTreeItemView view = new OrderTreeItemView(item);

                    switch (Utils.treeItemDepth(getTreeItem())) {

                        case 1: {

                            view.loadOrder();

                            break;
                        }

                        case 2: {

                            view.loadRestaurant(item.getItems().stream().map(orderItem -> orderItem.getFood().getRestaurant()).collect(Collectors.toList()).get(Utils.treeItemIndex(getTreeItem())));

                            break;
                        }

                        case 3: {

                            view.loadOrderItem(item.getItems().get(Utils.treeItemIndex(getTreeItem())));

                            break;
                        }

                    }

                    setGraphic(view);

                }

            }

        });

    }

    public void loadUser(UserData user) {

        this.user = user;

        fullName.setText(user.getFullName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());

        Global.daemonExecutor.submit(() -> {

            try {

                List<Order> orders = Global.FoodHub.ORDERS.getUserOrders(user);

                Platform.runLater(() -> setOrders(orders));

            } catch (Exception e) {

                e.printStackTrace();

            }

        });

        switcher.setCurrentNodeIndex(2);

        Global.daemonExecutor.submit(() -> Global.USER.PREFERENCES.replaceSavedEmail(user.getEmail()));

    }

    private void setOrders(List<Order> orders) {

        if(orders == null || orders.isEmpty()) {

            ordersSeparator.setText("No order submitted yet");
            Utils.setDeploy(this.orders, true);

        } else {

            ordersRoot.getChildren().clear();

            for (Order order : orders) {

                TreeItem<Order> orderTreeItem = new TreeItem<>(order);

                ordersRoot.getChildren().add(orderTreeItem);

                order.getItems().stream().map(orderItem -> orderItem.getFood().getRestaurant()).distinct().forEach(restaurant -> {

                    TreeItem<Order> restaurantTreeItem = new TreeItem<>(orderTreeItem.getValue());

                    orderTreeItem.getChildren().add(restaurantTreeItem);

                    order.getItems().stream().filter(orderItem -> orderItem.getFood().getRestaurant().equals(restaurant)).forEach(orderItem -> {

                        restaurantTreeItem.getChildren().add(new TreeItem<>(restaurantTreeItem.getValue()));

                    });

                });

            }

            Utils.setDeploy(this.orders, false);

        }

    }

    public UserData getUser() {

        return user;

    }

    public void logOut() {

        user = null;
        loadLoginPage();

    }

    public void loadLoginPage() {

        switcher.setCurrentNodeIndex(0);
        PrimaryController.instance().loadProfilePage();
        loginController.reset();

    }

    public void loadSignUpPage() {

        switcher.setCurrentNodeIndex(1);
        navigator.addNavi(() -> {

            switcher.setCurrentNodeIndex(1);
            return true;

        }, null);

    }

    public void updateOrders() {

        setOrders(Global.FoodHub.ORDERS.getUserOrders(user));

    }

}
