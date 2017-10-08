package com.android.DirectC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.android.DirectC.CustomAdapters.FileListAdapter;
import com.android.DirectC.Entities.Item;

public class FilePickerActivity extends ListActivity{
	
	private File currentDir;
	private String rootDirPath;
	private FileListAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_picker);
		
		listView = (ListView) findViewById(android.R.id.list);
		
		currentDir = Environment.getExternalStorageDirectory();
		rootDirPath = currentDir.getName();
		fillDirectory(currentDir);
	}
	
	public void fillDirectory(File file){
		//Set title to the current directory
		setTitle(file.getName());
		
		//Retrieve all files in this directory
		File[] dirs = file.listFiles();		
		
		//List of directories
		List<Item> directories = new ArrayList<Item>();
		//List if files
		List<Item> files = new ArrayList<Item>();
		
		for(File f : dirs){
			//Is a directory
			if(f.isDirectory()){
				File[] innerFiles = f.listFiles();
				int numItems;
				if(innerFiles!=null)
					numItems = innerFiles.length;
				else
					numItems = 0;
				
				Item item = new Item(Item.DIRECTORY, f.getName(), numItems, f.getAbsolutePath());
				directories.add(item);
			}
			//Is a file
			else{
				Item item = new Item(Item.FILE, f.getName(), f.length(), f.getAbsolutePath());
				files.add(item);
			}
		}
		
		directories.addAll(files);
		
		if(!currentDir.getName().equals(rootDirPath)){
			directories.add(0, new Item(Item.UP, "../", file.getParent()));
		}
		
		adapter = new FileListAdapter(this, directories);
		listView.setAdapter(adapter);		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Item item = (Item) adapter.getItem(position);
		
		int typeItem = item.getTypeItem();
		if(typeItem==Item.DIRECTORY || typeItem==Item.UP){
			currentDir = new File(item.getAbsolutePath());
			fillDirectory(currentDir);
		}
		else{
			chooseFile(item);
		}
	}
	
	public void chooseFile(final Item item){
		AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
		newDialog.setTitle("Send file");
		newDialog.setMessage("Are you sure you want to send this file?");
		
		newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = getIntent();
				intent.putExtra("filePath", item.getAbsolutePath());
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
		
		newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		newDialog.show();
	}
	
	@Override
	public void onBackPressed() {
		if(!currentDir.getName().equals(rootDirPath)){
			fillDirectory(currentDir.getParentFile());
			currentDir = currentDir.getParentFile();
		}
		else{
			finish();
		}
	}
}