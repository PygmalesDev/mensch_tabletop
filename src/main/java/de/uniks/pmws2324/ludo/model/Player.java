package de.uniks.pmws2324.ludo.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Player
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_CONES = "cones";
   public static final String PROPERTY_PLAYER_COLOR = "playerColor";
   public static final String PROPERTY_STARTING_POSITION = "startingPosition";
   public static final String PROPERTY_CONES_INITIAL_POSITIONS = "conesInitialPositions";
   private String name;
   private List<Cone> cones;
   protected PropertyChangeSupport listeners;
   private String playerColor;
   private Position startingPosition;
   private List<Position> conesInitialPositions;

   public String getName()
   {
      return this.name;
   }

   public Player setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public List<Cone> getCones()
   {
      return this.cones != null ? Collections.unmodifiableList(this.cones) : Collections.emptyList();
   }

   public Player withCones(Cone value)
   {
      if (this.cones == null)
      {
         this.cones = new ArrayList<>();
      }
      if (!this.cones.contains(value))
      {
         this.cones.add(value);
         value.setPlayer(this);
         this.firePropertyChange(PROPERTY_CONES, null, value);
      }
      return this;
   }

   public Player withCones(Cone... value)
   {
      for (final Cone item : value)
      {
         this.withCones(item);
      }
      return this;
   }

   public Player withCones(Collection<? extends Cone> value)
   {
      for (final Cone item : value)
      {
         this.withCones(item);
      }
      return this;
   }

   public Player withoutCones(Cone value)
   {
      if (this.cones != null && this.cones.remove(value))
      {
         value.setPlayer(null);
         this.firePropertyChange(PROPERTY_CONES, value, null);
      }
      return this;
   }

   public Player withoutCones(Cone... value)
   {
      for (final Cone item : value)
      {
         this.withoutCones(item);
      }
      return this;
   }

   public Player withoutCones(Collection<? extends Cone> value)
   {
      for (final Cone item : value)
      {
         this.withoutCones(item);
      }
      return this;
   }

   public String getPlayerColor()
   {
      return this.playerColor;
   }

   public Player setPlayerColor(String value)
   {
      if (Objects.equals(value, this.playerColor))
      {
         return this;
      }

      final String oldValue = this.playerColor;
      this.playerColor = value;
      this.firePropertyChange(PROPERTY_PLAYER_COLOR, oldValue, value);
      return this;
   }

   public Position getStartingPosition()
   {
      return this.startingPosition;
   }

   public Player setStartingPosition(Position value)
   {
      if (this.startingPosition == value)
      {
         return this;
      }

      final Position oldValue = this.startingPosition;
      if (this.startingPosition != null)
      {
         this.startingPosition = null;
         oldValue.setPlayer(null);
      }
      this.startingPosition = value;
      if (value != null)
      {
         value.setPlayer(this);
      }
      this.firePropertyChange(PROPERTY_STARTING_POSITION, oldValue, value);
      return this;
   }

   public List<Position> getConesInitialPositions()
   {
      return this.conesInitialPositions != null ? Collections.unmodifiableList(this.conesInitialPositions) : Collections.emptyList();
   }

   public Player withConesInitialPositions(Position value)
   {
      if (this.conesInitialPositions == null)
      {
         this.conesInitialPositions = new ArrayList<>();
      }
      if (!this.conesInitialPositions.contains(value))
      {
         this.conesInitialPositions.add(value);
         value.setInitialPlayer(this);
         this.firePropertyChange(PROPERTY_CONES_INITIAL_POSITIONS, null, value);
      }
      return this;
   }

   public Player withConesInitialPositions(Position... value)
   {
      for (final Position item : value)
      {
         this.withConesInitialPositions(item);
      }
      return this;
   }

   public Player withConesInitialPositions(Collection<? extends Position> value)
   {
      for (final Position item : value)
      {
         this.withConesInitialPositions(item);
      }
      return this;
   }

   public Player withoutConesInitialPositions(Position value)
   {
      if (this.conesInitialPositions != null && this.conesInitialPositions.remove(value))
      {
         value.setInitialPlayer(null);
         this.firePropertyChange(PROPERTY_CONES_INITIAL_POSITIONS, value, null);
      }
      return this;
   }

   public Player withoutConesInitialPositions(Position... value)
   {
      for (final Position item : value)
      {
         this.withoutConesInitialPositions(item);
      }
      return this;
   }

   public Player withoutConesInitialPositions(Collection<? extends Position> value)
   {
      for (final Position item : value)
      {
         this.withoutConesInitialPositions(item);
      }
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPlayerColor());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setStartingPosition(null);
      this.withoutConesInitialPositions(new ArrayList<>(this.getConesInitialPositions()));
      this.withoutCones(new ArrayList<>(this.getCones()));
   }
}
