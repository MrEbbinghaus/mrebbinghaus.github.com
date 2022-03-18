In this article, I will explain the core principles behind [Fulcro](https://github.com/fulcrologic/fulcro). 
I will show you how to organize state in a normalized graph database and how to write basic components to utilize it. 

For a complete run-trough I recommend reading the [Fulcro Book](https://book.fulcrologic.com/) or looking into the [community tutorials](https://fulcro-community.github.io/main/index.html).

## Your first components

Imagine, you are building a social-network kind of application, and you want to implement a profile page:

```clojure
(ns fulcro.tutorial
  (:require 
    [com.fulcorlogic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom :refer [div p h2 ul li img]]))

(defn profile-page []
  (div
    (p
      (h2 "Spouse")
      (div 
        (img {:src "alice.png"}) 
        "Alice")
    (p
      (h2 "My Friends")
      (ul
        (li (div 
             (img {:src "alice.png"}) 
             "Alice"))
        (li (div 
             (img {:src "bob.png"}) 
             "Bob"))
        (li (div 
             (img {:src "charlie.png"}) 
             "Charlie"))))))
```

But that's not why you are here. You are a programmer. You don't hard code information, and you don't write large functions. You want to break things apart and reuse them:

```clojure
(defn person [{:keys [name profile-pic-href]}]
  (div
    (img {:src profile-pic-href})
    name))

(defn friend-list [friends]
 (ul 
   (for [friend friends]
     (li (person friend)))))

(defn profile-page [{:keys [spouse friends]}]
  (div
    (p 
      (h2 "Spouse")
      (person spouse))
    (p
      (h2 "My Friends")
      (friend-list friends))))
```

Great, now you can feed the data like this to your function:

```clojure
(def data
    {:spouse {:name "Alice"
              :profile-pic-href "alice.png"}
     :friends [{:name "Alice"
               :profile-pic-href "alice.png"}
              {:name "Bob"
               :profile-pic-href "bob.png"}
              {:name "Charlie"
               :profile-pic-href "charlie.png"}]})
```

This looks fine for the _state_ of your client... BUT

Did you notice, that you have the data for your spouse twice?
What do you do, when you want to change the profile picture? 
You will have to remember every place where you put that link! Terrifying!

A function for this could look like this:

```clojure
(defn update-profile-pic [data {:keys [name new-profile-pic-href]}]
 (-> data
   (assoc-in [:spouse :profile-pic-href] new-profile-pic-href)
   (update :friends 
           #(map 
             (fn [friend]
               (if (= name (:name friend)
                 (assoc friend :profile-pic-href new-profile-pic-href)
                 friend)))
             %))))
```

## Data Normalization

But you can avoid this mess, by preventing the duplication. 

Imagine you organize your data with references like this:

```clojure
{:person {"Alice"
          {:name "Alice"
           :profile-pic-href "alice.png"}
          "Bob"
          {:name "Bob"
           :profile-pic-href "bob.png"}
          "Charlie"
          {:name "Charlie"
           :profile-pic-href "charlie.png"}}
 :spouse [:person "Alice"]
 :friends [[:person "Alice"] [:person "Bob"] [:person "Charlie"]]}
```
A vector like `[:person "Alice"]` is really nothing different like the vector you would use for `get-in`, `assoc-in` and `update-in`.

Now the ugly update function from above becomes as simple as:

```clojure
(defn update-profile-pic [data {:keys [name new-profile-pic-href]}]
 (update-in data [:person name] 
   assoc :profile-pic-href new-profile-pic-href)
```

But what about our render functions? They don't know or care about references or duplication. All they want is their data to render.

This is where Fulcro comes to the rescue. 
Fulcro is first and foremost only about solving this problem.
How to organize data so that they can be modified easily, but at the same time feed it to my render functions? 

## Introducing components and the `defsc` macro. 
Components are a representation of a piece of data in our state.
The `defsc` macro allows us to define the data a component depends on (_query_)
and how to the reference (called _ident_) to this data looks like.

```clojure
; the render function from above
(defn person [{:keys [name profile-pic-href]}]
  (div
    (img {:src profile-pic-href})
    name))

; becomes this:
(defsc Person [_ {:keys [name profile-pic-href]}]
  {:query [:name :profile-pic-href]
   :ident [:person :name]}
  (div
    (img {:src profile-pic-href})
    name))
```

Let us examine what we see here:

```clojure
(defsc Person 
  [_ ; ignore for now

   ;; Same as the parameters from the `person` function. This map contains the data we get.
   {:keys [name profile-pic-href] :as props}] 
  {;; This describes the data we depend on. No sense in passing you the `profile-pic-href` if you don't need it, for example.
   :query [:name :profile-pic-href]
   
   ;; this describes where this entity is located in your data
   ;; [:person :name] is actually a shorthand for:
   :ident (fn [] [:person (:name props)])}
  (div
    (img {:src profile-pic-href})
    name))
```

All well and good.. But how does the data actually get to the component? 
Let me simplify.

On the one hand, we had our data with the duplications. There were no idents, just nesting. That kind of format is called a tree. 

On the other hand, we would like to have our data _normalized_, meaning our data is _always_ located at the same position and only that position.

The query and idents helps us translate the one to the other and back.

```
tree = query + normalized-data
normalized-data = query + tree
```

Example:
```clojure 
(def spouse 
  {:name "Alice"
   :profile-pic-href "alice.png"})

;; Note: The functions in Fulcro are not called `normalize` and `denormalize`, but the idea is the same.

(def normalized-state
  (normalize ; called `comp/merge-component` in fulcro
    {}      ; The current state. The result of the normalization get deep-merged into this
    Person  ; Recipe how to normalize
    spouse) ; Data to normalize
;; => 
;; {:person {"Alice"
;;           {:name "Alice"
;;            :profile-pic-href "alice.png"}}}

(denormalize 
    [:person "Alice"] ; Where to begin the query?
    Person           ; Recipe how to denormalize
    normalized-state) ; Data to denormalize
;; => 
;; {:name "Alice"
;;  :profile-pic-href "alice.png"}

;; denormalize may look like:
(-> normalized-state
  (get-in [:person "Alice"])
  (select-keys (comp/get-query Person)))
```


### Composition
You may have noticed the line with the note: _Where to begin the query?_
In our application, we have _one_ normalized-state. So we want _one_ query to get _one_ tree to pass to our render function. 

Coming back to our complete code from before, we can replace the `person` function with our component:

```clojure
(defsc Person [_ {:keys [name profile-pic-href]}]
  {:query [:name :profile-pic-href]
   :ident [:person :name]}
  (div
    (img {:src profile-pic-href})
    name))

;; We need an actual function to call for rendering. The prefix `ui-` is idiomatic.
(def ui-person (comp/factory Person {:keyfn :name}))

(defn friend-list [friends]
 (ul 
   (for [friend friends]
     (li (ui-person friend)))))

(defn profile-page [{:keys [spouse friends]}]
  (div
    (p 
      (h2 "Spouse")
      (ui-person spouse))
    (p
      (h2 "My Friends")
      (friend-list friends))))
```

But the real _root_ of our application is still the `profile-page` function. That's the function that gets the _one tree_ to render. So this is the place where we need our _one query_ to start.

So let us compose:

```clojure
; First. Let's create a component for the ProfilePage:
(defsc ProfilePage [_ {:keys [spouse friends]}]

  ;; Now to the query. What do we want? (Spoilers: The props above.) 
  ;; But these props are not primitive data. They are data another component depens on, so we want to include the query of that component. (This is called a join)
  {:query [{:spouse (comp/get-query Person)}
           {:friends (comp/get-query Person)}]} ; note there is no special syntax for a to-many join. 
  (div
    (p 
      (h2 "Spouse")
      (ui-person spouse))
    (p
      (h2 "My Friends")
      (friend-list friends)))
```

#### Where is the `:ident`?
Not every component requires an ident. In the case of the _root_-component, it wouldn't even make sense. It would be like: `(get-in data [])`

### The Final Code
```clojure
(ns fulcro.tutorial
  (:require 
    [com.fulcorlogic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom :refer [div p h2 ul li img]]))

(defsc Person [_ {:keys [name profile-pic-href]}]
  {:query [:name :profile-pic-href]
   :ident [:person :name]}
  (div
    (img {:src profile-pic-href})
    name))

(def ui-person (comp/factory Person {:keyfn :name}))

(defn friend-list [friends]
 (ul 
   (for [friend friends]
     (li (ui-person friend)))))

(defsc ProfilePage [_ {:keys [spouse friends]}]
  {:query [{:spouse (comp/get-query Person)}
           {:friends (comp/get-query Person)}]} 
  (div
    (p 
      (h2 "Spouse")
      (ui-person spouse))
    (p
      (h2 "My Friends")
      (friend-list friends)))
```

## Follow-up words

Fulcro has a lot more _things_ going on. If you read about fulcro, keep in mind: The data normalization is the core thing you need to understand to use fulcro. You need to understand *how* and *why* queries and components get composed like they do, to have a chance to understand the rest. 

When you feel ready I urge you to go through this in the [Fulcro Book Chapter 4](https://book.fulcrologic.com/#_getting_started). There is a lot more you _can_ learn. 