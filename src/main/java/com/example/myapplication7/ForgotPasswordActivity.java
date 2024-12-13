package com.example.myapplication7;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText forgotEmail;
    private Button resetPasswordButton;
    private TextView tempPasswordText; // Ajout de la référence au TextView
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        forgotEmail = findViewById(R.id.forgotEmail);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        tempPasswordText = findViewById(R.id.tempPasswordText); // Lien avec le TextView
        databaseHelper = new DatabaseHelper(this);

        resetPasswordButton.setOnClickListener(v -> {
            String email = forgotEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Veuillez entrer un email", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Entrez un email valide", Toast.LENGTH_SHORT).show();
            } else {
                // Générer un mot de passe temporaire
                String temporaryPassword = generateTemporaryPassword();

                // Mettre à jour le mot de passe dans la base de données
                boolean isUpdated = databaseHelper.updatePassword(email, temporaryPassword);

                if (isUpdated) {
                    // Afficher le mot de passe temporaire dans le TextView
                    tempPasswordText.setText("Votre nouveau mot de passe est : " + temporaryPassword);
                } else {
                    tempPasswordText.setText(""); // Efface tout texte précédent
                    Toast.makeText(ForgotPasswordActivity.this, "Email non trouvé", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Méthode pour générer un mot de passe temporaire
    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) { // Génère un mot de passe de 8 caractères
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}