todo

# FRONTEND

- When the user is logged in, his feed is his home screen
- When the user is not logged in? Page with sign up form, or log in button
    + make new home-comp

- admin
    - posts
    - comments
        + finish refactoring admin.comments
        + namespace commments -> blog comments

- template dispatching by multimethod
    + why? it's a why to decentralize and make it more simple
    + elogger.views
    + admin.views

- make error messages more informative and user friendly

- load only n rows from the db; paginate, in order to load next
    + comments
    + posts
    + users
    + pages

- load blog-posts sorted by created-at
- load admin rows sorted by created-at
    + blog-posts
    + users
    + pages
    + comments

- Authorization
    + 1. I guess we can make it simpler by having the superuser, who can do whatever he wants, and
    + 2. Registered users who can edit whatever they created (his blog posts, and comments on his blog posts, for instance). They'll have access to the admin dashboard, but they'll only see what they are allowed to see (their stuff)
    + 2.1 Maybe it would be useful to have orgs (organizations), such that a user can manage authorization for other users for the posts of this org.


****

But I guess I am digressing. What do users want? I must always keep that question in mind. I am once again going for premature optimization. Instead, I should strive for the MVP (minimum viable product). I still don't have that. 

What is it that I am trying to build?

- Maybe a mix of Instagram with Twitch and OnlyFans
- What features am I missing to get there?
    + Right now the users cannot upload images and videos, which is central 
    + Right now the CMS is focused on the homepage and blog posts, and posts pages. of, instagram, twitch, are all focused on the user profiles. The user profile is where all the magic happens. Right now my user profile is shit.
    + I have no authorization. This is also central. I need the system to properly handle who can CRUD what properly.
    + User interaction is also something big. I guess what I'm lacking on this is that a user should be able to follow profiles and get whatever they're posting on his "home-page", like instagram (newer first).
    + The difference of what I've built, to what I need to build is that they'll have what I've built for each user, and the user profile will be their homepage