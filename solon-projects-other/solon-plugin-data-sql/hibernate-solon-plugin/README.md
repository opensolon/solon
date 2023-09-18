

* jpa 接口，尽量保持与 spring.data.jpa 兼容（有好几个 jap 需求的用户，提过这个）
  * 要从 spring 那边的接口定义重新 copy 一下。。。目前是从网上copy的，可能旧了
* 后面 jpa 接口可以独立为  solon.data.jpa
* 要完成与 @Tran 的事务对接（能与其它 orm 同时存在）