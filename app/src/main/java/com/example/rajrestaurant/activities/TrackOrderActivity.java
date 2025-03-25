package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TrackOrderActivity extends AppCompatActivity {
    TextView orderItems, orderAmount, orderAddress;
    ImageView deliveryPartnerImage;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String orderId;
    Button downloadInvoiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        Toolbar myToolbar = findViewById(R.id.track_order_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.back);
        myToolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Firestore and Auth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get references to TextViews
        orderItems = findViewById(R.id.order_items);
        orderAmount = findViewById(R.id.order_amount);
        orderAddress = findViewById(R.id.address);
        downloadInvoiceButton = findViewById(R.id.btn_download_invoice);

        // Retrieve orderId from intent
        orderId = getIntent().getStringExtra("orderId");

        // Fetch order details from Firestore
        if (orderId != null) {
            fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Order ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Handle invoice download
        downloadInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderId != null) {
                    generateInvoicePdf();
                } else {
                    Toast.makeText(TrackOrderActivity.this, "Order details are not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchOrderDetails(String orderId) {
        firestore.collection("Orders").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String address = document.getString("address");
                        double totalAmount = document.getDouble("totalAmount");
                        List<Map<String, Object>> items = (List<Map<String, Object>>) document.get("items");

                        // Set data to the respective views
                        orderAddress.setText(address);
                        orderAmount.setText("Total Amount: ₹" + totalAmount);
                        displayOrderItems(items);
                    } else {
                        Toast.makeText(TrackOrderActivity.this, "No such order", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TrackOrderActivity.this, "Failed to fetch order details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayOrderItems(List<Map<String, Object>> items) {
        StringBuilder itemDetails = new StringBuilder();
        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> item : items) {
                String productName = (String) item.get("productName"); // Assuming each item has a productName key
                String quantity = (String) item.get("totalQuantity");  // Quantity is stored as String

                Number totalPriceNumber = (Number) item.get("totalPrice"); // totalPrice could be Long or Double
                double totalPrice = totalPriceNumber.doubleValue(); // Convert to double

                if (productName != null && quantity != null && totalPriceNumber != null) {
                    // Add item details to the StringBuilder
                    itemDetails.append(productName)
                            .append(" x ")
                            .append(quantity)
                            .append(": ₹")
                            .append(totalPrice)
                            .append("\n");
                }
            }
            orderItems.setText(itemDetails.toString());
        } else {
            orderItems.setText("No items in this order");
        }
    }

    private void generateInvoicePdf() {
        // Set up the file path for the PDF
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Raj_Restaurant/";
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(this, "Failed to create directory.", Toast.LENGTH_SHORT).show();
            return;
        }

        String pdfFileName = "Invoice_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(directory, pdfFileName);

        try {
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Load logo
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_raj_red);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();

            ImageData imageData = ImageDataFactory.create(bitmapData);
            Image logo = new Image(imageData).setHeight(150).setWidth(200);

            // Add logo and title
            document.add(logo.setHorizontalAlignment(HorizontalAlignment.CENTER));
            document.add(new Paragraph("Raj Restaurant")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Add order details in tabular format
            float[] columnWidths = {200F, 300F}; // Define two columns
            Table orderDetailsTable = new Table(columnWidths);

            orderDetailsTable.addCell(new Paragraph("Order ID:").setBold());
            orderDetailsTable.addCell(new Paragraph(orderId));

            orderDetailsTable.addCell(new Paragraph("Address:").setBold());
            orderDetailsTable.addCell(new Paragraph(orderAddress.getText().toString()));

            document.add(orderDetailsTable);
            document.add(new Paragraph("\n"));

            // Add items in a table format
            float[] itemColumnWidths = {150F, 100F, 100F}; // Define three columns for items
            Table itemsTable = new Table(itemColumnWidths);

            // Add table headers
            itemsTable.addCell(new Paragraph("Item").setBold());
            itemsTable.addCell(new Paragraph("Quantity").setBold());
            itemsTable.addCell(new Paragraph("Price").setBold());

            // Iterate over items and add them to the table
            String[] orderItemsArray = orderItems.getText().toString().split("\n");
            for (String item : orderItemsArray) {
                String[] itemDetails = item.split(": ₹");  // Assuming "productName x quantity: ₹price" format
                if (itemDetails.length == 2) {
                    String[] nameAndQuantity = itemDetails[0].split(" x ");
                    if (nameAndQuantity.length == 2) {
                        itemsTable.addCell(new Paragraph(nameAndQuantity[0])); // Product name
                        itemsTable.addCell(new Paragraph(nameAndQuantity[1])); // Quantity
                        itemsTable.addCell(new Paragraph("₹" + itemDetails[1])); // Price
                    }
                }
            }

            document.add(itemsTable);
            document.add(new Paragraph("\n"));

            // Add total amount as a separate table row with larger font
            Table totalAmountTable = new Table(new float[]{100F});
            //totalAmountTable.addCell(new Paragraph("Total Amount:").setBold().setFontSize(16));
            totalAmountTable.addCell(new Paragraph("₹" + orderAmount.getText().toString()).setFontSize(16).setBold());

            document.add(totalAmountTable);

            // Close the document
            document.close();

            // Show success message
            Toast.makeText(this, "Invoice generated at: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Automatically open the PDF
            openPdf(pdfFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error writing PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void openPdf(File pdfFile) {
        Uri pdfUri = Uri.fromFile(pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "PDF downloaded successfully.", Toast.LENGTH_SHORT).show();
        }
    }
}
