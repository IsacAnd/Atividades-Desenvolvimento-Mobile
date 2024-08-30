import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.livrosv2.R

class BooksAdapter(private val books: List<Map<String, Any>>, private val onItemClick: (Map<String, Any>) -> Unit) :
    RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.titleView)
        val tvPublisher: TextView = itemView.findViewById(R.id.publisherView)
        val tvGenre: TextView = itemView.findViewById(R.id.genreView)
        val imageView: ImageView = itemView.findViewById(R.id.contactPhoto)
        val checkBoxRead: CheckBox = itemView.findViewById(R.id.readCheckbox)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.tvTitle.text = book["title"] as? String ?: "Unknown Title"
        holder.tvPublisher.text = book["publisher"] as? String ?: "Unknown Publisher"
        holder.tvGenre.text = book["genre"] as? String ?: "Unknown Genre"

        // Carregar a imagem usando Glide
        val imageUrl = book["imageUrl"] as? String
        if (imageUrl != null) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imageView)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.default_book_image)
                .into(holder.imageView)
        }

        // Configurar o CheckBox de "Lido"
        val isRead = book["isRead"] as? Boolean ?: false
        holder.checkBoxRead.isChecked = isRead

        // Configurar a RatingBar
        val rating = (book["rating"] as? Number)?.toFloat() ?: 0f
        holder.ratingBar.rating = rating

        // Configurar o clique no item
        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
}
