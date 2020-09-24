package com.myprescriptions.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.myprescriptions.DetailsActivity;
import com.myprescriptions.R;
import com.myprescriptions.models.Med;
import com.myprescriptions.models.Prescriptions;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.myprescriptions.Details.app_folder;
import static com.myprescriptions.Details.root;
import static com.myprescriptions.MainActivity.mProgressDialog;

public class PrescriptionsAdapter extends RecyclerView.Adapter<PrescriptionsAdapter.ImageViewHolder> implements Filterable {


    private Context mContext;
    private List<Prescriptions> PrescriptionsList;
    private List<Prescriptions> DefaultPrescriptionsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mMyPrescriptionsDatabase, mUsersDatabase;
    private FirebaseUser mFirebaseUser;
    private SearchAdapterListener listener;
    private String mCurrentUserId;
    Bitmap bmp, scalebmp;

    public PrescriptionsAdapter(Context context, List<Prescriptions> prescriptionsList, PrescriptionsAdapter.SearchAdapterListener listener) {
        mContext = context;
        PrescriptionsList = prescriptionsList;
        DefaultPrescriptionsList=prescriptionsList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_layout_my_prescription, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mCurrentUserId = mFirebaseUser.getUid();
        mMyPrescriptionsDatabase = FirebaseDatabase.getInstance().getReference().child("Prescriptions").child(mCurrentUserId);
        mMyPrescriptionsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.card_bg);
        scalebmp = Bitmap.createScaledBitmap(bmp, 1200, 325, false);

        final Prescriptions prescriptions = PrescriptionsList.get(position);
        holder.Doctor_Phone_Number.setText(prescriptions.getPhone_number());
        holder.Doctor_Name.setText(prescriptions.getName());
        holder.Details.setText(prescriptions.getDetails());
        holder.Timestamp.setText(prescriptions.getTimestamp());
        holder.More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.pdf:
                                createPDF("MyPrescription-"+System.currentTimeMillis(), prescriptions.getTimestamp(), prescriptions.getName(),
                                        prescriptions.getDetails(), prescriptions.getMedicine());
                                return true;
                            case R.id.call:
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+prescriptions.getPhone_number()));
                                mContext.startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_pdf);
                popupMenu.show();

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("prescription", prescriptions);
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });

        mProgressDialog.dismiss();

    }

    @Override
    public int getItemCount() {
        return PrescriptionsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    PrescriptionsList = DefaultPrescriptionsList;
                } else {
                    List<Prescriptions> filteredList = new ArrayList<>();
                    for (Prescriptions row : DefaultPrescriptionsList) {

                        if (row.getName().toLowerCase().contains(charString) ||
                                row.getDetails().toLowerCase().contains(charSequence) ||
                                row.getEmail().toLowerCase().contains(charSequence)
                        ) {
                            filteredList.add(row);
                        }
                    }

                    PrescriptionsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = PrescriptionsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                PrescriptionsList = (ArrayList<Prescriptions>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private TextView Doctor_Name, Doctor_Phone_Number, Details, Timestamp;
        private ImageView More;

        public ImageViewHolder(View itemView) {
            super(itemView);

            Doctor_Name = itemView.findViewById(R.id.prescription_doctor_name);
            Doctor_Phone_Number = itemView.findViewById(R.id.prescription_doctor_phone_number);
            Details = itemView.findViewById(R.id.prescription_details);
            Timestamp = itemView.findViewById(R.id.prescription_timestamp);
            More = itemView.findViewById(R.id.prescription_more);

        }
    }

    public interface SearchAdapterListener {
        void onSearchSelected(Prescriptions prescription);
    }

    private void createPDF(String file_name, String timestamp, String Doctor_Name, String details, ArrayList<Med> medicine){

        Document doc = new Document();
        PdfWriter docWriter = null;

        try {

            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            String path = app_folder +file_name+".pdf";
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));

            doc.addAuthor("AMtudio");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("MyPrescription.com");
            doc.addTitle("My Prescription");
            doc.setPageSize(PageSize.A4);

            //open document
            doc.open();

            Chunk titleChunk = new Chunk("My Prescriptions", bf12);
            // Creating Paragraph to add...
            Paragraph titleParagraph = new Paragraph(titleChunk);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            doc.add(titleParagraph);

            //create a paragraph
            Paragraph timestamp_text = new Paragraph("Date - Time",bfBold12);
            doc.add(timestamp_text);

            Paragraph timestamp_ = new Paragraph(" "+timestamp);
            doc.add(timestamp_);

            Paragraph doctor_name_text = new Paragraph("Doctor Name", bfBold12);
            doc.add(doctor_name_text);

            Paragraph doctor_name = new Paragraph(" "+Doctor_Name);
            doc.add(doctor_name);

            Paragraph patient_reason_text = new Paragraph("Reason", bfBold12);
            doc.add(patient_reason_text);

            Paragraph patient_reason = new Paragraph(" "+details);
            doc.add(patient_reason);

            Paragraph empty = new Paragraph(" ");

            //specify column widths
            float[] columnWidths = {
                    2f,
                    2f,
                    2f,
                    2f,
                    2f,
                    2f
            };
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(90f);

            insertCell(table, "Medicine Name", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Morning", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Afternoon", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Evening", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Night", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "Quantity", Element.ALIGN_CENTER, 1, bfBold12);
            table.setHeaderRows(1);

            for (int x = 0; x < medicine.size(); x++) {

                insertCell(table, medicine.get(x).getName(), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, medicine.get(x).getMor(), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, medicine.get(x).getAft(), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, medicine.get(x).getEve(), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, medicine.get(x).getNig(), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, medicine.get(x).getQuantity(), Element.ALIGN_CENTER, 1, bf12);

            }
            
            empty.add(table);
            
            doc.add(empty);

            Toast.makeText(mContext, "PDF Saved.", Toast.LENGTH_SHORT).show();

        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }


    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }
}