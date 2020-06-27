# DemoProject
Hi there! This is my Demo Project which I made to showcase some of my skills and also as a best practises reminder for myself:)

So essentialy it has a simple functionality. It will load a list of githhub users and show it in an endless RecyclerView ( well, not so endless but a bit later on this).
All loaded items are cached in database so once you loaded it you can access it without network, obviously they won't be updated in this mode but will be loaded from cache.

You can select one of the filters in top right to filter items based on their type: User or Organisation. 
Also you can click on any item to open its details screen.

### Technologies
- Kotlin
- Architecture Components
- Coroutines
- Room
- Retrofit
- Dagger
- Paging Library
- And for tests:
  - Espresso
  - Mockito
  - Hamcrest
  - Robolectric
  
### Screeenshots of the app
![Screenshot of the app](https://i.imgur.com/cHtm594.png) ![Screenshot of the app](https://i.imgur.com/MdTiEqH.png)

## And a pinch of salt 
Unfortunately I ran into 2 problems with github API:
1. Rate limit is quite low so most likely after using app for some time you will start getting "Couldn't update items" messages. 
This means that rate limit was exceeded and everything is now loading from DB. (of course if there is no other network issue present)
2. Well, I wanted to implement filters and User and Organisation types seemed to fit the idea well but github only gives 0-2 organisations per page of 30 items. 
I think you can guess the problem here: with organisations filter on you will basically never load a full page because there is a problem número uno 
and it would mean indeaquate loading times. Imagine loading 30+ pages consecuently while user waits for it. 
So what I did, is basically try to load first few pages until I get 0 organisations on page ( which happens fast:) ) 
and then as last resort try to load everything that was ever in db.
Not a dream but also a working solution so I decided to go with it for now.
