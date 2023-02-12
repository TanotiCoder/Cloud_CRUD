package com.example.cloudcrud.crud.ui

import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cloudcrud.common.data_model.FirestoreDataModel
import com.example.cloudcrud.crud.viewmodel.CrudViewModel


@Composable
fun MainScreen(modifier: Modifier, viewModel: CrudViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()


    Scaffold(
        Modifier.fillMaxSize(), floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    viewModel.openFor = "add"
                    viewModel.openDialog()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

        }, floatingActionButtonPosition = FabPosition.End
    ) {
        CrudUI(viewModel, modifier.padding(it))

        AddItemAlertBox(
            alertState = viewModel.dialogState,
            onDismiss = {
                viewModel.closeDialog()
                viewModel.clearAll()
            },
            onConfirm = {
                if (viewModel.openFor == "add") viewModel.addItem() else viewModel.updateItem()
                viewModel.closeDialog()
                viewModel.clearAll()
            },
            titleSend = { viewModel.getTitle = it },
            descriptionSend = { viewModel.getDescription = it },
            title = viewModel.getTitle,
            description = viewModel.getDescription,
            modifier = modifier,
            scrollState = scrollState
        )
    }
}


@Composable
fun CrudUI(viewModel: CrudViewModel, modifier: Modifier) {
    val showState = viewModel.stateShow.value
    val loadState = viewModel.stateLoad.value
    val context = LocalContext.current

    Column(modifier.fillMaxSize()) {
        if (showState.isLoadingShow) {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (showState.errorShow.isNotBlank()) {
            Text(text = "You Get Error")
        } else {
            if (showState.dataShow.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Tap '+' to add New Item")
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = showState.dataShow) { firestoreDataModel ->
                        CardView(
                            firestoreDataModel = firestoreDataModel,
                            clickOnDelete = { id -> viewModel.deleteItem(id) },
                            setTitle = { viewModel.getTitle = it },
                            setDescription = { viewModel.getDescription = it },
                            setId = { viewModel.getId = it },
                            clickOnEdit = {
                                viewModel.openFor = "edit"
                                viewModel.openDialog()
                            }
                        )
                    }
                }
            }
        }
        if (loadState.errorLoad.isNotBlank()) {
            Toast.makeText(context, loadState.errorLoad, Toast.LENGTH_SHORT).show()
            "".also { loadState.errorLoad = it }
        } else if (loadState.dataLoad.isNotBlank()) {
            Toast.makeText(context, loadState.dataLoad, Toast.LENGTH_SHORT).show()
            "".also { loadState.dataLoad = it }
        }
    }

}

@Composable
fun AddItemAlertBox(
    alertState: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleSend: (String) -> Unit,
    descriptionSend: (String) -> Unit,
    title: String,
    description: String,
    modifier: Modifier,
    scrollState: ScrollState
) {
    if (alertState) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Add Item In Store") },
            text = {
                Column(
                    modifier = modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TextField(value = title,
                        onValueChange = titleSend,
                        singleLine = true,
                        placeholder = { Text(text = "Title") })
                    TextField(value = description,
                        onValueChange = descriptionSend,
                        modifier = modifier
                            .heightIn(48.dp, 64.dp)
                            .verticalScroll(scrollState, reverseScrolling = true),
                        placeholder = { Text(text = "Description") })
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    enabled = title.isNotBlank() && description.isNotBlank()
                ) {
                    Text(text = "Done")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            },
            shape = RoundedCornerShape(5.dp)
        )
    }
}

@Composable
fun CardView(
    firestoreDataModel: FirestoreDataModel,
    clickOnDelete: (id: String) -> Unit,
    setTitle: (title: String) -> Unit,
    setDescription: (description: String) -> Unit,
    setId: (id: String) -> Unit,
    clickOnEdit: () -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = 4.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = firestoreDataModel.item.title ?: "Null",
                    fontSize = 32.sp,
                    maxLines = 2
                )
                Text(
                    text = firestoreDataModel.item.description ?: "Null",
                    fontSize = 18.sp,
                    maxLines = 5
                )
            }
            Column(Modifier.fillMaxHeight()) {
                IconButton(onClick = {
                    clickOnDelete(firestoreDataModel.id)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = {
                    setTitle(firestoreDataModel.item.title!!)
                    setDescription(firestoreDataModel.item.description!!)
                    setId(firestoreDataModel.id)
                    clickOnEdit()
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}