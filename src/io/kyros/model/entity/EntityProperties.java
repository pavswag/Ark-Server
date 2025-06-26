package io.kyros.model.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EntityProperties {
    SUPERIOR(1) {
        @Override
        public void apply(Entity entity) {
            entity.getHealth().setMaximumHealth((int) (entity.getHealth().getMaximumHealth() * 1.50));
            entity.getHealth().setCurrentHealth(entity.getHealth().getMaximumHealth());
        }
    },
    MINI(1) {
        @Override
        public void apply(Entity entity) {
            entity.getHealth().setMaximumHealth((int) (entity.getHealth().getMaximumHealth() * 0.65));
            entity.getHealth().setCurrentHealth(entity.getHealth().getMaximumHealth());
        }
    },
    SHADOW(1) {
        @Override
        public void apply(Entity entity) {
            entity.getHealth().setMaximumHealth((int) (entity.getHealth().getMaximumHealth() * 1.15));
            entity.getHealth().setCurrentHealth(entity.getHealth().getMaximumHealth());
        }
    },
    LUKE(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Luke here
        }
    },
    PROPHET(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Prophet here
        }
    },
    SPONGE(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Sponge here
        }
    },
    THE_GURU(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for The Guru here
        }
    },
    BBQ(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for BBQ here
        }
    },
    CEREDORIS(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Ceredoris here
        }
    },
    THRILL(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Thrill here
        }
    },
    THIRTEENTH_REASON(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for 13th Reason here
        }
    },
    ADRIAN(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Adrian here
        }
    },
    BURNSY(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Burnsy here
        }
    },
    ALPHA(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Alpha here
        }
    },
    APARIGRAHA(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Aparigraha here
        }
    },
    HEIMDALL(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Heimdall here
        }
    },
    NOVACHRONO(1) {
        @Override
        public void apply(Entity entity) {
            // Define behavior for Novachrono here
        }
    },
    ;
    public int graphicsId;
    public abstract void apply(Entity entity);

}