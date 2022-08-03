#include <proc.h>
#include <elf.h>
#include <fs.h>
#ifdef __LP64__
# define Elf_Ehdr Elf64_Ehdr
# define Elf_Phdr Elf64_Phdr
# define ELF_Off  Elf64_Off
#else
# define Elf_Ehdr Elf32_Ehdr
# define Elf_Phdr Elf32_Phdr
# define ELF_Off  Elf32_Off
#endif

#if defined(__ISA_AM_NATIVE__)
# define EXPECT_TYPE EM_X86_64
#elif defined(__ISA_X86__)
# define EXPECT_TYPE EM_386
#elif defined(__ISA_MIPS32__)
# define EXPECT_TYPE EM_MIPS
#elif defined(__ISA_RISCV32__) || defined(__ISA_RISCV64__)
# define EXPECT_TYPE EM_RISCV
#elif
# error unsupported ISA __ISA__
#endif

void init_ramdisk();
size_t ramdisk_read(void *buf, size_t offset, size_t len);
size_t ramdisk_write(const void *buf, size_t offset, size_t len);
size_t get_ramdisk_size();

static void read(int fd, void *buf, size_t offset, size_t len){
  fs_lseek(fd, offset, SEEK_SET);
  fs_read(fd, buf, len);
}


#define NR_PAGE 8
#define PAGESIZE 4096

 __attribute__ ((__used__)) static void * alloc_section_space(AddrSpace *as, uintptr_t vaddr, size_t p_memsz){
  //size_t page_n = p_memsz % PAGESIZE == 0 ? p_memsz / 4096 : (p_memsz / 4096 + 1);
  size_t page_n = ((vaddr + p_memsz - 1) >> 12) - (vaddr >> 12) + 1;
  void *page_start = new_page(page_n);

  printf("Loaded Segment from [%x to %x)\n", vaddr, vaddr + p_memsz);
  
  for (int i = 0; i < page_n; ++i){
    // TODO: 这里prot参数不规范
    map(as, (void *)((vaddr & ~0xfff) + i * PAGESIZE), (void *)(page_start + i * PAGESIZE), 1);
  }

  return page_start;
}

#define MAX(a, b)((a) > (b) ? (a) : (b))

static uintptr_t loader(PCB *pcb, const char *filename) {
  int fd = fs_open(filename, 0, 0);
  if (fd == -1){ 
    assert(0); //filename指向文件不存在
  }
  
  AddrSpace *as = &pcb->as;
  
  Elf_Ehdr elf_header;
  read(fd, &elf_header, 0, sizeof(elf_header));
  //根据小端法 0x7F E L F
  assert(*(uint32_t *)elf_header.e_ident == 0x464c457f);
  
  ELF_Off program_header_offset = elf_header.e_phoff;
  size_t headers_entry_size = elf_header.e_phentsize;
  int headers_entry_num = elf_header.e_phnum;

  for (int i = 0; i < headers_entry_num; ++i){
    Elf_Phdr section_entry;
    read(fd, &section_entry, 
      i * headers_entry_size + program_header_offset, sizeof(elf_header));
    void *phys_addr;
    uintptr_t virt_addr;
    switch (section_entry.p_type) {
    case PT_LOAD:
      //virt_addr = (void *)section_entry.p_vaddr; 
      // phys_addr = (void *)alloced_page_start + (section_entry.p_vaddr - 0x40000000); // 这里是把0x40000000加载到他对应的实际地址
      virt_addr = section_entry.p_vaddr;
      phys_addr = alloc_section_space(as, virt_addr, section_entry.p_memsz);

      // printf("Load to %x with offset %x\n", phys_addr, section_entry.p_offset);
      //做一个偏移
      read(fd, phys_addr + (virt_addr & 0xfff), section_entry.p_offset, section_entry.p_filesz);
      //同样做一个偏移
      memset(phys_addr + (virt_addr & 0xfff) + section_entry.p_filesz, 0, 
        section_entry.p_memsz - section_entry.p_filesz);
      
      if (section_entry.p_filesz < section_entry.p_memsz){// 应该是.bss节
        //做一个向上的4kb取整数
        // if (pcb->max_brk == 0){
        printf("Setting .bss end %x\n", section_entry.p_vaddr + section_entry.p_memsz);
        // 我们虽然用max_brk记录了最高达到的位置，但是在新的PCB中，我们并未在页表目录中更新这些信息，oH，所以就会失效啦。
        // 于是我们就做了一些权衡。
        //pcb->max_brk = MAX(pcb->max_brk, ROUNDUP(section_entry.p_vaddr + section_entry.p_memsz, 0xfff));
        //TODO: Trade-off
        pcb->max_brk = ROUNDUP(section_entry.p_vaddr + section_entry.p_memsz, 0xfff);
        
        // }
      }
      
      break;
    
    default:
      break;
    }

  }
  
  printf("Entry: %p\n", elf_header.e_entry);
  return elf_header.e_entry;
}

// static uintptr_t loader(PCB *pcb, const char *filename) {
//   Elf_Ehdr elf;
//   ramdisk_read(&elf, 0, sizeof(Elf_Ehdr));
//   assert(*(uint32_t *)elf.e_ident == 0x464C457F);//F L E 0x7f
  
//   Elf_Phdr *phdr = (Elf_Phdr*)malloc(sizeof(Elf_Phdr) * elf.e_phnum);
//   ramdisk_read(phdr, elf.e_phoff, sizeof(Elf_Phdr) * elf.e_phnum);
  
//   for (int i = 0; i < elf.e_phnum; i++) {
//     if(phdr[i].p_type != PT_LOAD) continue;
//     ramdisk_read((char*)phdr[i].p_vaddr, phdr[i].p_offset, phdr[i].p_filesz);//read program
//     memset((char*)phdr[i].p_vaddr + phdr[i].p_filesz, 0, phdr[i].p_memsz - phdr[i].p_filesz);//set data 0
//   }
  
//   return elf.e_entry;
// }

// static uintptr_t loader(PCB *pcb, const char *filename) {
//   // Log("[Loader] ELF file is reading from '%s'.", filename);
//   // ELF Header
//   Elf_Ehdr *elf = (Elf_Ehdr*)malloc(sizeof(Elf_Ehdr));
//   ramdisk_read(elf, 0, sizeof(Elf_Ehdr));
//   // Check ELF magic number
//   Log("elf->e_ident = 0x%p", *(uint32_t *)elf->e_ident);
//   assert(*(uint32_t *)elf->e_ident == 0x464C457F);
//   // Checl ELF machine
//   Log("elf->e_machine = 0x%p", elf->e_machine);
//   assert(EXPECT_TYPE == elf->e_machine);
//   // Program Header
//   Elf_Phdr *phdr = (Elf_Phdr*)malloc(sizeof(Elf_Phdr) * elf->e_phnum);
//   ramdisk_read(phdr, elf->e_phoff, sizeof(Elf_Phdr) * elf->e_phnum);
//   // Analysis of program header table
//   for (int i = 0; i < elf->e_phnum; ++i) {
//     if (phdr[i].p_type != PT_LOAD) continue;
//     ramdisk_read((char*)phdr[i].p_vaddr, phdr[i].p_offset, phdr[i].p_filesz);
//     memset((char*)phdr[i].p_vaddr + phdr[i].p_filesz, 0, phdr[i].p_memsz - phdr[i].p_filesz);
//   }
//   return elf->e_entry;
// }


void naive_uload(PCB *pcb, const char *filename) {
  uintptr_t entry = loader(pcb, filename);
  Log("Jump to entry = %p", entry);
  ((void(*)())entry) ();
}

