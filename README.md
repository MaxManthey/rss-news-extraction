# rss-news-persistence

This project extracts articles from html files and saves them to an H2 database it creates on its own.    
(This is a follow up project to [rss-news-downloader](https://github.com/MaxManthey/rss-news-downloader))
  
### Run

To run the project, make sure to add a connection String for your Database [first arg] and the folder where your JSON files are saved [second arg].

### Error Output

If an error occurs, a `output.log` file will be created on the root level of the project.
