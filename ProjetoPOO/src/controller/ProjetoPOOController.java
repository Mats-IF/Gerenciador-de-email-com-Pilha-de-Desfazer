package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Stack;

public class ProjetoPOOController {

    @FXML
    private TextField txtNovoEmail;

    @FXML
    private Button btnCadastrarEmail;

    @FXML
    private ListView<String> lvEmailsNaoLidos;

    @FXML
    private ListView<String> lvEmailsLidos;

    @FXML
    private Button btnMoverParaLidos;

    @FXML
    private Button btnMoverParaNaoLidos;

    @FXML
    private Button btnExcluirEmail;

    @FXML
    private Button btnDesfazer;

    @FXML
    private Label lblStatus;

    // Listas de e-mails
    private ObservableList<String> emailsNaoLidos;
    private ObservableList<String> emailsLidos;

    // Pilha para desfazer ações
    private Stack<Acoes> pilhaDesfazer;

    @FXML
    public void initialize() {
        emailsNaoLidos = FXCollections.observableArrayList();
        emailsLidos = FXCollections.observableArrayList();

        lvEmailsNaoLidos.setItems(emailsNaoLidos);
        lvEmailsLidos.setItems(emailsLidos);

        pilhaDesfazer = new Stack<>();

        atualizarEstadoBotoes();
    }

    @FXML
    void cadastrarEmail(ActionEvent event) {
        String novoEmail = txtNovoEmail.getText().trim();
        if (!novoEmail.isEmpty()) {
            emailsNaoLidos.add(novoEmail);
            pilhaDesfazer.push(new Acoes("cadastrar", novoEmail, null));
            lblStatus.setText("E-mail cadastrado: " + novoEmail);
            txtNovoEmail.clear();
        } else {
            lblStatus.setText("O campo de e-mail está vazio.");
        }
        atualizarEstadoBotoes();
    }

    @FXML
    void moverParaLidos(ActionEvent event) {
        String emailSelecionado = lvEmailsNaoLidos.getSelectionModel().getSelectedItem();
        if (emailSelecionado != null) {
            emailsNaoLidos.remove(emailSelecionado);
            emailsLidos.add(emailSelecionado);
            pilhaDesfazer.push(new Acoes("moverParaLidos", emailSelecionado, "naoLidos"));
            lblStatus.setText("E-mail movido para lidos: " + emailSelecionado);
        } else {
            lblStatus.setText("Selecione um e-mail para mover.");
        }
        atualizarEstadoBotoes();
    }

    @FXML
    void moverParaNaoLidos(ActionEvent event) {
        String emailSelecionado = lvEmailsLidos.getSelectionModel().getSelectedItem();
        if (emailSelecionado != null) {
            emailsLidos.remove(emailSelecionado);
            emailsNaoLidos.add(emailSelecionado);
            pilhaDesfazer.push(new Acoes("moverParaNaoLidos", emailSelecionado, "lidos"));
            lblStatus.setText("E-mail movido para não lidos: " + emailSelecionado);
        } else {
            lblStatus.setText("Selecione um e-mail para mover.");
        }
        atualizarEstadoBotoes();
    }

    @FXML
    void excluirEmail(ActionEvent event) {
        String emailSelecionado = lvEmailsNaoLidos.getSelectionModel().getSelectedItem();
        if (emailSelecionado == null) {
            emailSelecionado = lvEmailsLidos.getSelectionModel().getSelectedItem();
            if (emailSelecionado != null) {
                emailsLidos.remove(emailSelecionado);
                pilhaDesfazer.push(new Acoes("excluir", emailSelecionado, "lidos"));
            }
        } else {
            emailsNaoLidos.remove(emailSelecionado);
            pilhaDesfazer.push(new Acoes("excluir", emailSelecionado, "naoLidos"));
        }

        if (emailSelecionado != null) {
            lblStatus.setText("E-mail excluído: " + emailSelecionado);
        } else {
            lblStatus.setText("Selecione um e-mail para excluir.");
        }
        atualizarEstadoBotoes();
    }

    @FXML
    void desfazer(ActionEvent event) {
        if (!pilhaDesfazer.isEmpty()) {
            Acoes ultimaAcao = pilhaDesfazer.pop();
            switch (ultimaAcao.getTipo()) {
                case "cadastrar":
                    emailsNaoLidos.remove(ultimaAcao.getEmail());
                    lblStatus.setText("Ação desfeita: Cadastro de " + ultimaAcao.getEmail());
                    break;
                case "moverParaLidos":
                    emailsLidos.remove(ultimaAcao.getEmail());
                    emailsNaoLidos.add(ultimaAcao.getEmail());
                    lblStatus.setText("Ação desfeita: Movimento para lidos de " + ultimaAcao.getEmail());
                    break;
                case "moverParaNaoLidos":
                    emailsNaoLidos.remove(ultimaAcao.getEmail());
                    emailsLidos.add(ultimaAcao.getEmail());
                    lblStatus.setText("Ação desfeita: Movimento para não lidos de " + ultimaAcao.getEmail());
                    break;
                case "excluir":
                    if (ultimaAcao.getListaOrigem().equals("naoLidos")) {
                        emailsNaoLidos.add(ultimaAcao.getEmail());
                    } else {
                        emailsLidos.add(ultimaAcao.getEmail());
                    }
                    lblStatus.setText("Ação desfeita: Exclusão de " + ultimaAcao.getEmail());
                    break;
            }
        } else {
            lblStatus.setText("Não há ações para desfazer.");
        }
        atualizarEstadoBotoes();
    }

    private void atualizarEstadoBotoes() {
        btnMoverParaLidos.setDisable(lvEmailsNaoLidos.getSelectionModel().getSelectedItem() == null);
        btnMoverParaNaoLidos.setDisable(lvEmailsLidos.getSelectionModel().getSelectedItem() == null);
        btnExcluirEmail.setDisable(
                lvEmailsNaoLidos.getSelectionModel().getSelectedItem() == null &&
                        lvEmailsLidos.getSelectionModel().getSelectedItem() == null
        );
        btnDesfazer.setDisable(pilhaDesfazer.isEmpty());
    }

    // Classe para registrar as ações realizadas
    private static class Acoes {
        private final String tipo;
        private final String email;
        private final String listaOrigem;

        public Acoes(String tipo, String email, String listaOrigem) {
            this.tipo = tipo;
            this.email = email;
            this.listaOrigem = listaOrigem;
        }

        public String getTipo() {
            return tipo;
        }

        public String getEmail() {
            return email;
        }

        public String getListaOrigem() {
            return listaOrigem;
        }
    }
}
