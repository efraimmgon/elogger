todo

# FRONTEND

- admin
    - posts
    - comments
        + finish refactoring admin.comments
        + namespace commments -> blog comments

- template dispatching by multimethod
    + why? it's a why to decentralize and make it more simple
    + laconic-cms.views
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


****

But I guess I am digressing. What do users want? I must always keep that question in mind. I am once again going for premature optimization. Instead, I should strive for the MVP (minimum viable product). I still don't have that. 

What is it that I am trying to build?

- Maybe a mix of Instagram with Twitch and OnlyFans