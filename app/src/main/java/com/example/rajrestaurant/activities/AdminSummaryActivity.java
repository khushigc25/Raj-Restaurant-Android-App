package com.example.rajrestaurant.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rajrestaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminSummaryActivity extends AppCompatActivity {

    private EditText orderDateEditText;
    private Button generatePdfButton;
    private FirebaseFirestore db;
    private CollectionReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_summary);

        orderDateEditText = findViewById(R.id.edit_text_date);
        generatePdfButton = findViewById(R.id.btn_generate_pdf);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        ordersRef = db.collection("Orders");

        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdf();
            }
        });
    }

    private String formatDate(String inputDate) {
        // Expected format from input (MM/dd/yyyy)
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        // Firestore date format (MM dd, yyyy)
        SimpleDateFormat firestoreFormat = new SimpleDateFormat("MM dd, yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            return firestoreFormat.format(date); // Return formatted date
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void generatePdf() {
        String date = orderDateEditText.getText().toString();

        date = formatDate(date);

        if (!date.isEmpty()) {
            // Fetch orders for the specific date
            ordersRef.whereEqualTo("orderDate", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        createPdf(task.getResult());
                    } else {
                        showToast("No orders found for the given date.");
                    }
                }
            });
        } else {
            showToast("Please enter a date.");
        }
    }

    private void createPdf(QuerySnapshot orders) {

        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Raj_Restaurant/";
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            showToast("Failed to create directory.");
            return;
        }

        // Create a new PDF file
        String pdfFileName = "Orders_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(directory, pdfFileName);

        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Load logo from drawable
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_raj_red);
            com.itextpdf.io.source.ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();

            ImageData imageData = ImageDataFactory.create(bitmapData);
            Image logo = new Image(imageData).setHeight(150).setWidth(200);

            // Title and Restaurant Name
            Paragraph resName = new Paragraph("Raj Restaurant")
                    .setFontSize(28)
                    .setFontColor(new DeviceRgb(240, 79, 95))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);

            // Bill Receipt Heading
            Paragraph title = new Paragraph("Orders Summary for " + orderDateEditText.getText().toString())
                    .setFontSize(24)
                    .setFontColor(new DeviceRgb(240, 79, 95))
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);

            // Add Logo, Restaurant Name, and Title to document
            document.add(logo.setHorizontalAlignment(HorizontalAlignment.CENTER));
            document.add(resName);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Add a table with adjusted column widths for better visibility
            float[] columnWidths = {90F, 120F, 110F, 150F, 90F, 110F};  // Adjusted column widths
            Table table = new Table(columnWidths);

            // Add table headers
            table.addCell(new Paragraph("Order ID").setBold());
            //table.addCell(new Paragraph("User ID").setBold());
            table.addCell(new Paragraph("User Name").setBold());
            table.addCell(new Paragraph("User Mobile").setBold());
            table.addCell(new Paragraph("Address").setBold());
            table.addCell(new Paragraph("Total Amount").setBold());
            table.addCell(new Paragraph("Order Time").setBold());

            // Add order details row by row
            for (QueryDocumentSnapshot documentSnapshot : orders) {
                String orderId = documentSnapshot.getString("orderId");
                //String userId = documentSnapshot.getString("userId");
                String userName = documentSnapshot.getString("userName");
                String userMobile = documentSnapshot.getString("userMobile");
                String address = documentSnapshot.getString("address");
                Double totalAmount = documentSnapshot.getDouble("totalAmount");  // Ensure this field exists
                String orderTime = documentSnapshot.getString("orderTime");

                // Add each field to the table
                table.addCell(new Paragraph(orderId != null ? orderId : ""));
                //table.addCell(new Paragraph(userId != null ? userId : ""));
                table.addCell(new Paragraph(userName != null ? userName : ""));
                table.addCell(new Paragraph(userMobile != null ? userMobile : ""));
                table.addCell(new Paragraph(address != null ? address : ""));
                table.addCell(new Paragraph(totalAmount != null ? "₹" + totalAmount : ""));  // Handle potential null value
                table.addCell(new Paragraph(orderTime != null ? orderTime : ""));
            }

            // Add the table to the document
            document.add(table);

            // Close document
            document.close();
            showToast("PDF generated: " + pdfFile.getAbsolutePath());

            // Open the PDF automatically
            openPdf(pdfFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showToast("Error generating PDF: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error writing PDF: " + e.getMessage());
        }
    }



    private void openPdf(File pdfFile) {
        Uri pdfUri = Uri.fromFile(pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Check if there's an app available to handle PDF
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToast("Pdf downloaded successfully");
        }
    }

    private void showToast(String message) {
        Toast.makeText(AdminSummaryActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
